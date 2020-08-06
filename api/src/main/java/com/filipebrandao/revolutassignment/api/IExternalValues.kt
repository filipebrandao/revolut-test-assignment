package com.filipebrandao.revolutassignment.api

/**
 * Interface for the external values that should be provided to the NetworkLayer upon initialization
 */
interface IExternalValues {
    fun getBaseUrl(): String
}
