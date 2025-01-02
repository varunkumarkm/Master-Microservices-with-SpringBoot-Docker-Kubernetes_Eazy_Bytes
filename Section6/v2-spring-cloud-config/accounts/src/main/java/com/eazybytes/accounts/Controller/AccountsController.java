package com.eazybytes.accounts.Controller;

import com.eazybytes.accounts.Dto.AccountsContactInfoDto;
import com.eazybytes.accounts.Dto.CustomerDto;
import com.eazybytes.accounts.Dto.ErrorResponseDto;
import com.eazybytes.accounts.Dto.ResponseDto;
import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.service.IAccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "CRUD REST APIs for Accounts in EazyBank",
    description = "CRUD REST APIs for Accounts in EazyBank to CREATE, FETCH, UPDATE and DELETE account details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class AccountsController {

    public AccountsController(IAccountsService iAccountsService){
        this.iAccountsService=iAccountsService;
    }

    private final IAccountsService iAccountsService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @Operation(
        summary = "Create Account REST API",
        description = "REST API to create new Customer & Account inside Eazy Bank"
    )
    @ApiResponses({
        @ApiResponse(
        responseCode = "201",
        description = "Http status created"
        ),
        @ApiResponse(
        responseCode = "500",
        description = "Http status internal server error",
        content = @Content(
            schema = @Schema(implementation = ErrorResponseDto.class)
        )
        )
        }
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto){
        iAccountsService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }
    @Operation(
            summary = "Fetch Account REST API",
            description = "REST API to fetch Customer and Account details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http status internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam
                          @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                          String mobileNumber){
        CustomerDto customerDto = iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }
    @Operation(
            summary = "Update Account Details REST API",
            description = "REST API to update Customer and Account details based on a account number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http status ok"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Exception Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http status internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto){
        boolean isUpdate = iAccountsService.updateAccount(customerDto);
        if(isUpdate){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }
    @Operation(
            summary = "Delete Account & Customer Details REST API",
            description = "REST API to delete Customer and Account details based on mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http status ok"
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Exception Failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http status internal server error",
                    content = @Content(
                         schema = @Schema(implementation = ErrorResponseDto.class)
            )
            )
    }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam
                      @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                      String mobileNumber){
        boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
        if(isDeleted){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }
    @Operation(
            summary = "Get Build Information",
            description = "Get Build Information that is deployed into accounts microservice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @Operation(
            summary = "Get java version",
            description = "Get java version details that is installed in accounts microservice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/java-version-info")
    public ResponseEntity<String> getJavaVersionInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @Operation(
            summary = "Get Contact Info",
            description = "Contact Info details that can be reached out in case of any issues"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Http Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }
}

