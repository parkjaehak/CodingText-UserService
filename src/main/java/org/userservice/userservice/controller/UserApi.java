package org.userservice.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.dto.adminclient.AnnounceDetailResponse;
import org.userservice.userservice.dto.codebankclient.UserScoreRequest;
import org.userservice.userservice.dto.user.*;
import org.userservice.userservice.error.ErrorResponse;

@Tag(name = "User", description = "회원관리 API")
public interface UserApi {
    @Operation(summary = "사용자 문제풀이 통계 정보 조회",
            description = "사용자가 해결한 문제, 정식 등록된 문제, 점수, 등수에 대한 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserStatisticResponse.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우", description = "해당하는 ID를 가진 사용자를 찾지 못하였을 경우", value = """
                                    {
                                        "status":"404",
                                        "code": "U001",
                                        "message": "사용자를 찾을 수 없습니다."
                                    }
                                    """),
                    }
                    )),
            @ApiResponse(responseCode = "500", description = "기타 바인딩 에러로 인한 조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "binding error", description = "기타 바인딩 에러가 발생하였을 경우", value = """
                                    {
                                      "status": 500,
                                      "code": "BINDING_ERROR",
                                      "message": "Binding error occurred",
                                      "errors": [
                                        {
                                          "field": "nickName",
                                          "value": "nickname with spaces",
                                          "reason": "닉네임에는 공백이 포함될 수 없습니다."
                                        },
                                        {
                                          "field": "profileMessage",
                                          "value": "This status message is too long!",
                                          "reason": "상태 메세지는 최대 30자여야 합니다."
                                        }
                                      ]
                                    }
                                                                   
                                    """),
                    }
                    ))
    })
    ResponseEntity<?> findUserStatistics(String userId);

    @Operation(summary = "사용자 정보 조회",
            description = "내 정보 수정 탭에서 확인할 수 있는 사용자 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우", description = "해당하는 ID를 가진 사용자를 찾지 못하였을 경우", value = """
                                    {
                                        "status":"404",
                                        "code": "U001",
                                        "message": "사용자를 찾을 수 없습니다."
                                    }
                                    """),
                    }
                    ))
    })
    ResponseEntity<?> findUserInfos(String userId);

    @Operation(summary = "사용자 정보 수정",
            description = "내 정보 수정 탭에서 수정한 사용자 정보를 업데이트 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "사용자를 찾을 수 없는 경우", description = "해당하는 ID를 가진 사용자를 찾지 못하였을 경우", value = """
                                    {
                                        "status":"404",
                                        "code": "U001",
                                        "message": "사용자를 찾을 수 없습니다."
                                    }
                                    """),
                    }
                    )),
            @ApiResponse(responseCode = "500", description = "수정 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class), examples = {
                            @ExampleObject(name = "파일 업로드에 실패한 경우", description = "파일 업로드 실패하였을 경우", value = """
                                    {
                                        "status":"500",
                                        "code": "C006",
                                        "message": "파일 업로드에 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "binding error", description = "기타 바인딩 에러가 발생하였을 경우", value = """
                                    {
                                      "status": 500,
                                      "code": "BINDING_ERROR",
                                      "message": "Binding error occurred",
                                      "errors": [
                                        {
                                          "field": "nickName",
                                          "value": "nickname with spaces",
                                          "reason": "닉네임에는 공백이 포함될 수 없습니다."
                                        },
                                        {
                                          "field": "profileMessage",
                                          "value": "This status message is too long!",
                                          "reason": "상태 메세지는 최대 30자여야 합니다."
                                        }
                                      ]
                                    }
                                                                   
                                    """),
                    }
                    ))
    })
    ResponseEntity<?> updateUserInfos(
            @Parameter(description =
                    "1. 닉네임 \n" +
                            "2. 상태 메세지 \n" +
                            "3. 기본 프로그래밍 언어", required = true) UserInfoRequest userInfoRequest,
            @Parameter(description = "4. 프로필 사진")
            MultipartFile file, String userId);




    @Operation(summary = "블로그 서비스에서 사용할 유저 정보 조회",
            description = "블로그 서비스로부터 feign client 를 통해 요청을 받으면 해당 서비스에서 필요한 유저 정보를 전달한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 유저 정보를 조회",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoForBlogResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    ResponseEntity<?> findUserInfoForBlogService(String userId);


    @Operation(summary = "일반 사용자 공지사항 목록 조회",
            description = "일반 사용자가 공지사항 목록을 조회할 경우 관리자 서비스에 feign client 요청을 통해 페이징 된 목록을 가져온다.",
            parameters = {
                    @Parameter(name = "page", description = "조회할 페이지 번호 (기본값: 0)", schema = @Schema(type = "integer")),
                    @Parameter(name = "size", description = "페이지 당 데이터 개수 (기본값: 10)", schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항목록 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "공지사항을 조회하지 못했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    ResponseEntity<?> getAnnouncementsFromAdminService(int page, int size);



    @Operation(summary = "일반사용자 공지사항 상세내역 조회",
            description = "일반 사용자가 공지사항 상세내역을 조회할 경우 관리자 서비스에 feign client 요청을 통해 상세 정보를 가져온다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 상세정보 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnounceDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "공지사항을 조회하지 못했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 오류",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    ResponseEntity<?> getAnnouncementDetailsFromAdminService(@Parameter(description = "공지사항 ID") long announceId);


    @Operation(summary = "사용자의 점수를 바탕으로 티어와 전체 순위 업데이트",
            description = "코드 서비스에서 주어진 사용자 ID와 점수를 바탕으로 사용자의 티어와 전체 순위를 업데이트한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "점수 및 티어 업데이트 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> updateScore(UserScoreRequest userScoreRequest);


    @Operation(summary = "회원 탈퇴", description = "header로 전달된 UserId에 해당하는 User를 삭제한다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDeletionResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> deleteUser(String userId);
}
