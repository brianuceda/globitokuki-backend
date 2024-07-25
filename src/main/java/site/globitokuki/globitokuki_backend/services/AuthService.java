package site.globitokuki.globitokuki_backend.services;

import site.globitokuki.globitokuki_backend.dtos.AuthRequestDTO;
import site.globitokuki.globitokuki_backend.dtos.AuthResponseDTO;
import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;

public interface AuthService {
  public ResponseDTO login(AuthRequestDTO request);
  public ResponseDTO register(AuthRequestDTO request);
  public ResponseDTO verify(AuthResponseDTO request);
}
