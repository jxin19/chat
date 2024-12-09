package com.chat.homework.member.ui

import com.chat.homework.member.application.MemberCommandService
import com.chat.homework.member.application.MemberQueryService
import com.chat.homework.member.ui.dto.MemberRequest
import com.chat.homework.member.ui.dto.MemberResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "유저 관련 API", description = "유저 정보를 관리하는 API")
@RestController
@RequestMapping("/member")
class MemberController(
    private val memberQueryService: MemberQueryService,
    private val memberCommandService: MemberCommandService
) {

    @Operation(summary = "유저 상세 조회", description = "ID를 사용하여 유저의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): MemberResponse {
        val response = memberQueryService.getMemberById(id)
        return MemberResponse.of(response)
    }

    @Operation(summary = "유저 생성", description = "새로운 유저를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: MemberRequest): MemberResponse {
        val response = memberCommandService.create(request.toServiceDto())
        return MemberResponse.of(response)
    }

    @Operation(summary = "유저 수정", description = "ID를 사용하여 기존 유저 정보를 수정합니다.")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: MemberRequest
    ): MemberResponse {
        val response = memberCommandService.update(id, request.toServiceDto())
        return MemberResponse.of(response)
    }

}
