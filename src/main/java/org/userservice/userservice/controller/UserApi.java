package org.userservice.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.user.UserInfoRequest;
import org.userservice.userservice.dto.user.UserInfoResponse;
import org.userservice.userservice.dto.user.UserStatisticResponse;

@Tag(name = "User", description = "회원관리 API")
public interface UserApi {

    @Operation(summary = "사용자 문제풀이 통계 정보 조회",
            description = "사용자가 해결한 문제, 정식 등록된 문제, 점수, 등수에 대한 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user statistics.",
                    content = @Content(schema = @Schema(implementation = UserStatisticResponse.class)))
    })
    ResponseEntity<?> findUserStatistics();

    @Operation(summary = "사용자 정보 조회",
            description = "내 정보 수정 탭에서 확인할 수 있는 사용자 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user information.",
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class)))
    })
    ResponseEntity<?> findUserInfos();

    @Operation(summary = "사용자 정보 수정",
            description = "내 정보 수정 탭에서 수정한 사용자 정보를 업데이트 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user information.",
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class)))
    })
    ResponseEntity<?> updateUserInfos(
            @Parameter(description =
                    "1. 닉네임 \n" +
                    "2. 상태 메세지 \n" +
                    "3. 기본 프로그래밍 언어", required = true) UserInfoRequest userInfoRequest,
            @Parameter(description = "4. 프로필 사진")
            MultipartFile file);
}
