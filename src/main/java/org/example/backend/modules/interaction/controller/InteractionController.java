package org.example.backend.modules.interaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.PageResult;
import org.example.backend.common.util.UserContext;
import org.example.backend.dto.interaction.BorrowApplyDTO;
import org.example.backend.dto.interaction.CommentDTO;
import org.example.backend.dto.interaction.RatingDTO;
import org.example.backend.modules.interaction.service.InteractionService;
import org.example.backend.vo.interaction.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 互动控制器
 */
@RestController
@RequestMapping("/api/interaction")
@Tag(name = "互动管理", description = "评分、收藏、评论接口")
public class InteractionController {
    
    @Autowired
    private InteractionService interactionService;
    
    @PostMapping("/rate")
    @Operation(summary = "评分")
    public ApiResponse<Void> rate(@Valid @RequestBody RatingDTO ratingDTO) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.rate(ratingDTO, userId);
        return ApiResponse.<Void>success("评分成功", null);
    }
    
    @PostMapping("/favorite/{bookId}")
    @Operation(summary = "收藏/取消收藏")
    public ApiResponse<Void> toggleFavorite(@PathVariable Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.toggleFavorite(bookId, userId);
        return ApiResponse.<Void>success("操作成功", null);
    }
    
    @PostMapping("/comment")
    @Operation(summary = "发表评论")
    public ApiResponse<Void> addComment(@Valid @RequestBody CommentDTO commentDTO) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.addComment(commentDTO, userId);
        return ApiResponse.<Void>success("评论发表成功", null);
    }
    
    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "删除评论")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.deleteComment(commentId, userId);
        return ApiResponse.<Void>success("删除成功", null);
    }
    
    @GetMapping("/comments")
    @Operation(summary = "分页查询评论")
    public ApiResponse<PageResult<CommentVO>> listComments(
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<CommentVO> result = interactionService.listComments(bookId, page, size);
        return ApiResponse.success(result);
    }
    
    @PostMapping("/borrow/{bookId}")
    @Operation(summary = "申请借阅图书（管理员审核）")
    public ApiResponse<Void> borrowBook(@PathVariable Long bookId, @Valid @RequestBody BorrowApplyDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.borrowBook(bookId, userId, dto.getExpectedReturnTime());
        return ApiResponse.<Void>success("已提交借阅申请，请等待管理员审核", null);
    }
    
    @PostMapping("/return/{borrowId}")
    @Operation(summary = "申请归还图书（管理员确认）")
    public ApiResponse<Void> returnBook(@PathVariable Long borrowId) {
        Long userId = UserContext.getCurrentUserId();
        interactionService.returnBook(borrowId, userId);
        return ApiResponse.<Void>success("已提交还书申请，请等待管理员确认", null);
    }
}

