import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapitest.data.api.RetrofitClient
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.data.model.Location
import com.example.myapitest.data.repository.VehicleRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val repository: VehicleRepository = VehicleRepository(apiService = RetrofitClient.apiService),
): ViewModel() {

    private val _vehicle = MutableLiveData(Vehicle("", "", "", "", "",
        Location(0.0, 0.0)
    ))
    val vehicle: LiveData<Vehicle> get() = _vehicle

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedImageUri = MutableLiveData<Uri?>(null)
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> get() = _currentLocation

    fun addVehicle(vehicle: Vehicle) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.addNewVehicle(vehicle)
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.lastLocation.addOnSuccessListener { place: android.location.Location? ->
            place?.let {
                _currentLocation.postValue(
                   Location(
                        it.latitude,
                        it.longitude
                    )
                )
            }
        }
    }

    fun updateVehicleName(name: String) {
        _vehicle.value = _vehicle.value?.copy(name = name)
    }

    fun updateVehicleYear(year: String) {
        _vehicle.value = _vehicle.value?.copy(year = year)
    }

    fun updateVehicleLicence(licence: String) {
        _vehicle.value = _vehicle.value?.copy(licence = licence)
    }

    fun updateVehicleLocation(location: Location) {
        _vehicle.value = _vehicle.value?.copy(place= location)
        _currentLocation.value = location
    }
}
