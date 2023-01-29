package com.mx.alexis.gpssensor

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mx.alexis.gpssensor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        enableLocation()
    }

    //Metodo para saber si el permiso esta aceptado
    //Regresa un true o false
    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED //Permiso aceptado

    //Si el permiso esta dado ahora comprobar si esta activado el servicio
    //Metodo para saber si el servicio esta activo si no el usuario lo debe activar
    private fun enableLocation() {
        if (isLocationPermissionGranted()) {
            //Ya los tiene aceptados
            //open sensor
            binding.buttonLocalizar.setOnClickListener {
                readLocation()
            }
        } else {
            //Pedir que acepte el permiso
            requestLocationPermission()
        }
    }

    private fun readLocation() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    val location: Location? = it.result
                    if (location == null) {
                        Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        binding.latitude.text = location.latitude.toString()
                        binding.longitud.text = location.longitude.toString()
                        binding.accuracy.text = location.accuracy.toString()


                    }
                }
            }
        }
    }

    //Pedirle al usuario que acepte los permisos
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //Aqui ya se habian pedido los permisos y el usuario los rechazó
            Toast.makeText(this, "ve a ajustes y acepta permisos", Toast.LENGTH_SHORT).show()

        } else {
            // Es la primera vez que pedimos permisos
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )  //Pide permisos
        }
    }

    //Metodo para capturar la respuesta del usuario cuando acepta los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // open sensor
                binding.buttonLocalizar.setOnClickListener {
                    readLocation()
                }
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localizacion ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }


}