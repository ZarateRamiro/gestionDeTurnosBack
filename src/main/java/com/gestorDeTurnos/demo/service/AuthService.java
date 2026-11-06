package com.gestorDeTurnos.demo.service;

import com.gestorDeTurnos.demo.dto.LoginRequestDTO;
import com.gestorDeTurnos.demo.dto.LoginResponseDTO;
import com.gestorDeTurnos.demo.dto.RegistroRequestDTO;

public interface AuthService {
  LoginResponseDTO login(LoginRequestDTO request);
  LoginResponseDTO registrar(RegistroRequestDTO request);
}
