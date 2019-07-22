package com.info.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.*

import java.util.Locale


/**
 * A simple [Fragment] subclass.
 */
class LocationFragment : Fragment() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var btnLocation: android.widget.Button? = null
    private var txtLocation: TextView? = null
    internal var isGPS: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.txtLocation = view.findViewById(R.id.txtLocation)
        this.btnLocation = view.findViewById(R.id.btnLocation)

        initLocationClient()
        btnLocation!!.setOnClickListener { getLocation() }
    }

    fun initLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = (10 * 1000).toLong() // 10 seconds
        locationRequest!!.fastestInterval = (5 * 1000).toLong() // 5 seconds

        GpsUtils(activity!!).turnGPSOn { isGPSEnable ->
            // turn on GPS
            isGPS = isGPSEnable
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for (location in locationResult!!.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        txtLocation!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                    }
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
            }
        }
    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                GpsUtils.LOCATION_REQUEST
            )

        } else {
            mFusedLocationClient!!.lastLocation.addOnSuccessListener(activity!!) { location ->
                if (location != null) {
                    wayLatitude = location.latitude
                    wayLongitude = location.longitude
                    txtLocation!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                } else {
                    mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient!!.lastLocation.addOnSuccessListener(activity!!) { location ->
                        if (location != null) {
                            wayLatitude = location.latitude
                            wayLongitude = location.longitude
                            txtLocation!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                        } else {
                            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
                        }
                    }

                } else {
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GpsUtils.GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }
}// Required empty public constructor
