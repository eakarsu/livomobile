namespace java tr.com.eno.livo.thrift.authc

include "shared.thrift"

typedef shared.AuthenticationToken AuthenticationToken

struct CompanyAuthenticationResult {

    1: required bool success;
    2: optional string message;
    3: optional AuthenticationToken authenticationToken;
    4: required string companyId;
    5: required string appName;
}

struct UserAuthenticationResult {

    1: required bool success;
    2: optional string message;
    3: optional AuthenticationToken authenticationToken;
    4: required string userPrincipal;
    5: required string appName;
}

struct DeviceAuthenticationResult {

    1: required bool success;
    2: optional string message;
    3: optional AuthenticationToken authenticationToken;
    4: required string deviceId;
    5: required string appName;
}

struct SettingsAuthenticationResult {

    1: required string settingKey;
    2: required string appName;
    3: required string settingVal;
    4: required string settingId;
}

service AuthenticationService {

    SettingsAuthenticationResult authenticateSetting(1:string appName);

    CompanyAuthenticationResult authenticateCompany(1:string companyId, 2:string companySecret, 3:string appName);

    UserAuthenticationResult authenticateUser(1:AuthenticationToken companyAuthenticationToken, 2:string userPrincipal, 3:string userCredentials, 4:string appName);

    DeviceAuthenticationResult authenticateDevice(1:AuthenticationToken UserAuthenticationToken, 2:string deviceId, 3:string appName)

    void deauthenticate(1:AuthenticationToken token);
}