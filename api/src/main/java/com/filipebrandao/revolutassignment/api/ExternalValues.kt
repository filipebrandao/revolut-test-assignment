package com.filipebrandao.revolutassignment.api

/**
 * Hardcoded implementation for the [IExternalValues] interface
 * TODO: This can be improved by, instead of having hardcoded values, fetching them from the [BuildConfig]
 * TODO: which would allow for these values to change based on specific build variants
 */
class ExternalValues : IExternalValues {
    override fun getBaseUrl(): String {
        // TODO: don't use hardcoded string
        return "https://hiring.revolut.codes"
    }
}
