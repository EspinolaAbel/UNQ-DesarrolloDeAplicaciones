package ar.edu.unq.desapp.grupoN.desapp.webservice.dto

data class LoginCredentialsResponse(val accessToken: String, val type: String = "Bearer")