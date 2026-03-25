package com.legalaid.userauth.service.client;

import ch.qos.logback.core.net.server.Client;
import com.legalaid.userauth.dto.response.client.ClientProfileResponse;
import com.legalaid.userauth.dto.request.client.ClientProfileRequest;

import java.util.*;

public interface ClientService {
    ClientProfileResponse.ProfileResponse registerRequest(ClientProfileRequest.RegisterRequest registerRequest);
    ClientProfileResponse.ProfileResponse getClient(UUID clientId);
    ClientProfileResponse.ProfileResponse updateClient(ClientProfileRequest.UpdateRequest updateRequest);
}
