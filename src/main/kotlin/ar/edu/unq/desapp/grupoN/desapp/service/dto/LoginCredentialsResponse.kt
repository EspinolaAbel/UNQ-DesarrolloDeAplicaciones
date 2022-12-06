package ar.edu.unq.desapp.grupoN.desapp.service.dto

data class LoginCredentialsResponse(val accessToken: String, val type: String = "Bearer")