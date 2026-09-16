package com.devaz.minimallauncher.model

/**
 * Modèle représentant un contact téléphonique.
 */
data class ContactInfo(
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null
)
