package com.example.myapitest.utils

class PhoneNumber {
    companion object {
        fun formatPhoneNumber(phone: String): String {
            var cleanPhone = phone
            if (phone.startsWith("55"))
                cleanPhone = phone.substring(2)

            return if (cleanPhone.startsWith("+55")) cleanPhone else "+55$cleanPhone"
        }
    }
}