package com.training.resource.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceNormalReq;
import com.training.resource.entity.request.TagReq;
import com.training.resource.service.ResourceNormalService;
import com.training.resource.service.TagService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
@Validated
public class ResourceController {

    private final TagService tagService;
    private final ResourceNormalService resourceNormalService;

    // 创建资源分类标签
    @GetMapping("/v3/tag")
    public MsgRespond createTag(@Validated @RequestBody TagReq req) {
        return tagService.createTag(req);
    }

    // 编辑指定资源分类标签
    @PostMapping("/v3/tag/name/{tag_id}")
    public MsgRespond editTag(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段")
                              Integer tag_id,
                              @RequestParam("tag_name") @NotBlank(message = "tag_name字段不能为空") String tag_name) {
        return tagService.updateTag(tag_id, tag_name);
    }

    // 删除指定资源分类标签
    @DeleteMapping("/v3/tag/{tag_id}")
    public MsgRespond deleteTagById(@PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段")
                                    Integer tag_id) {
        return tagService.deleteTagByTagId(tag_id);
    }

    // 查看指定部门下的资源分类标签列表
    @GetMapping("/v1/tag/{dept_id}")
    public DataRespond getTagList(@PathVariable
                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                  Integer dept_id) {
        return tagService.getTagListByDeptId(dept_id);
    }

    // 上传资源文件
    @PostMapping("/v1/file/normal")
    public MsgRespond uploadResourceNormalFile(@Validated @ModelAttribute ResourceNormalReq req) {
        return resourceNormalService.uploadResourceNormalFile(req);
    }

    // 获取指定部门和分类标签的上传资源列表
    @GetMapping("/v1/file/normal/{dept_id}/{tag_id}/{page_size}/{offset}")
    public DataRespond getResourceNormalList(@PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                             Integer dept_id, @PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段")
                                             Integer tag_id, @PathVariable
                                             @Min(value = 1, message = "page_size必须为大于1的整数")
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                             Integer page_size,
                                             @PathVariable
                                             @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                             Integer offset) {
        return resourceNormalService.getResourceNormalList(dept_id, tag_id, page_size, offset);
    }

    // 下载指定资源文件
    @GetMapping("/v1/normal/file/{rid}")
    public ResponseEntity<?> downloadResourceNormalFile(@PathVariable
                                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                                        Integer rid) {
        return resourceNormalService.downloadResourceNormalFile(rid);
    }

    // 获取指定资源文件详情
    @GetMapping("/v1/normal/file/info/{rid}")
    public DataRespond getResourceNormalInfo(@PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                             Integer rid) {
        return resourceNormalService.getResourceNormalDetail(rid);
    }
}
