package com.tarun.sos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.splunk.mint.Mint
import com.tarun.sos.fragments.BottomNavigationDrawer
import com.tarun.sos.utils.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val SMS_PERMISSION_REQUEST_CODE = 2
        private const val CALL_PERMISSION_REQUEST_CODE = 3
        lateinit var emergencyContacts: ArrayList<EmergencyContacts>
        var defaultCallingContact: EmergencyContacts? = null
    }

    private var locationPermissionDenied = false
    private var smsPermissionDenied = false
    private var callPermissionDenied = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private val showNavigationDrawer: DrawRoundedBottomSheet by lazy {
        DrawRoundedBottomSheet(
            BottomNavigationDrawer(),
            "BottomNavigation"
        )
    }

    private val bottomAppBar: BottomAppBar by lazy { findViewById(R.id.bottom_app_bar) }
    private val sendSos: FloatingActionButton by lazy { findViewById(R.id.sos_button) }
    private val alarmSound: Uri by lazy { RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE) }
    private val mp: MediaPlayer by lazy { MediaPlayer.create(this, alarmSound) }
    private val am: AudioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var currentAudioVolume: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mint.initAndStartSession(this.application, "5b09dda5")

        setContentView(R.layout.activity_main)

        emergencyContacts = getEmergencyContacts(this)
        defaultCallingContact = getEmergencyCallContact(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        currentAudioVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        bottomAppBar.setNavigationOnClickListener {
            showNavigationDrawer.show(supportFragmentManager, "BottomNavigationSheet")
        }

        sendSos.setOnClickListener {
            enableAlarm()
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0 ?: return
        enableMyLocation()
    }

    private fun enableAlarm() {
        if (mp.isPlaying) {
            mp.pause()
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentAudioVolume, 0)
        } else {
            am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )
            mp.isLooping = true
            mp.seekTo(0)
            mp.start()
            enableSMSSending()
            enableCalling()
        }
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { it: Location? ->
                    if (it != null) {
                        location = it
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), 16F
                            )
                        )
                    } else {
                        val sb = Snackbar.make(
                            this,
                            findViewById(R.id.root_layout),
                            "Failed to get current location. Please enable Location from Settings",
                            Snackbar.LENGTH_INDEFINITE
                        )
                        sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                        sb.show()
                    }
                }
        } else {
            requestPermission(
                this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
    }

    private fun enableSMSSending() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val sms = SmsManager.getDefault()
            val message =
                "SOS Requested, Please find me at below location:\n https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
            for (emergency in emergencyContacts) {
                if (emergency.phone.isNotBlank())
                    sms.sendTextMessage(emergency.phone, null, message, null, null)
            }
            val sb = Snackbar.make(
                this,
                findViewById(R.id.root_layout),
                "SOS Sent to all your Emergency Contacts",
                Snackbar.LENGTH_LONG
            )
            sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            sb.show()
        } else {
            requestPermission(this, SMS_PERMISSION_REQUEST_CODE, Manifest.permission.SEND_SMS, true)
        }
    }

    private fun enableCalling() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val callingContact = getEmergencyCallContact(this)
            if (callingContact != null && callingContact.phone.isNotBlank()) {
                val callUri = Uri.parse("tel:${callingContact.phone}")
                val callIntent = Intent(Intent.ACTION_CALL).also {
                    it.data = callUri
                }
                startActivity(callIntent)
            }
        } else {
            requestPermission(
                this,
                CALL_PERMISSION_REQUEST_CODE,
                Manifest.permission.CALL_PHONE,
                true
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE && requestCode != SMS_PERMISSION_REQUEST_CODE && requestCode != CALL_PERMISSION_REQUEST_CODE) {
            return
        }
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (isPermissionGranted(
                        permissions,
                        grantResults,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    enableMyLocation()
                } else {
                    locationPermissionDenied = true
                }
            }
            SMS_PERMISSION_REQUEST_CODE -> {
                if (isPermissionGranted(
                        permissions,
                        grantResults,
                        Manifest.permission.SEND_SMS
                    )
                ) {
                    enableSMSSending()
                } else {
                    smsPermissionDenied = true
                }
            }
            CALL_PERMISSION_REQUEST_CODE -> {
                if (isPermissionGranted(
                        permissions,
                        grantResults,
                        Manifest.permission.CALL_PHONE
                    )
                ) {
                    enableCalling()
                } else {
                    callPermissionDenied = true
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        enableMyLocation()
        when {
            locationPermissionDenied -> {
                val sb = Snackbar.make(
                    this,
                    findViewById(R.id.root_layout),
                    "Failed to get current location. Please enable Location from Settings",
                    Snackbar.LENGTH_INDEFINITE
                )
                sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                sb.show()
                locationPermissionDenied = false
            }
            smsPermissionDenied -> {
                val sb = Snackbar.make(
                    this,
                    findViewById(R.id.root_layout),
                    "Failed to send your current location to your contacts. Please enable SMS Permissions",
                    Snackbar.LENGTH_INDEFINITE
                )
                sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                sb.show()
                smsPermissionDenied = false
            }
            callPermissionDenied -> {
                val sb = Snackbar.make(
                    this,
                    findViewById(R.id.root_layout),
                    "Failed to call your emergency contact. Please enable Phone Permissions",
                    Snackbar.LENGTH_INDEFINITE
                )
                sb.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                sb.show()
                callPermissionDenied = false
            }
        }
    }

}