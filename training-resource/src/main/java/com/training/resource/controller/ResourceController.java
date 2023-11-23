package com.training.resource.controller;

import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceLessonReq;
import com.training.resource.entity.request.ResourceNormalReq;
import com.training.resource.entity.request.ResourceNoteReq;
import com.training.resource.entity.request.TagReq;
import com.training.resource.service.ResourceLessonService;
import com.training.resource.service.ResourceNormalService;
import com.training.resource.service.ResourceNoteService;
import com.training.resource.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
@Validated
public class ResourceController {

    private final TagService tagService;
    private final ResourceNormalService resourceNormalService;
    private final ResourceLessonService resourceLessonService;
    private final ResourceNoteService resourceNoteService;

    // 创建资源分类标签
    @GetMapping("/v3/tag")
    public MsgRespond createTag(@Validated @RequestBody TagReq req) {
        return tagService.createTag(req);
    }

    // 编辑指定资源分类标签
    @PostMapping("/v3/tag/name/{tag_id}")
    public MsgRespond editTag(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段") Integer tag_id, @RequestParam("tag_name") @NotBlank(message = "tag_name字段不能为空") String tag_name) {
        return tagService.updateTag(tag_id, tag_name);
    }

    // 删除指定资源分类标签
    @DeleteMapping("/v3/tag/{tag_id}")
    public MsgRespond deleteTagById(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段") Integer tag_id) {
        return tagService.deleteTagByTagId(tag_id);
    }

    // 查看指定部门下的资源分类标签列表
    @GetMapping("/v1/tag/{dept_id}")
    public DataRespond getTagList(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段") Integer dept_id) {
        return tagService.getTagListByDeptId(dept_id);
    }

    // 上传资源文件
    @PostMapping("/v1/file/normal")
    public MsgRespond uploadResourceNormalFile(@Validated @ModelAttribute ResourceNormalReq req) {
        if (Objects.isNull(req.getResource_file())) {
            return MsgRespond.fail("上传文件不能为空");
        }
        return resourceNormalService.uploadResourceNormalFile(req);
    }

    // 获取指定部门和分类标签的上传资源列表
    @GetMapping("/v1/file/normal/{dept_id}/{tag_id}/{page_size}/{offset}")
    public DataRespond getResourceNormalList(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段") Integer dept_id, @PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段") Integer tag_id, @PathVariable @Min(value = 1, message = "page_size必须为大于1的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer page_size, @PathVariable @Min(value = 0, message = "offset必须为大于或等于0的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer offset) {
        return resourceNormalService.getResourceNormalList(dept_id, tag_id, page_size, offset);
    }

    // 下载指定资源文件
    @GetMapping("/v1/normal/file/{rid}")
    public ResponseEntity<?> downloadResourceNormalFile(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "rid必须为纯数字字段") Integer rid) {
        return resourceNormalService.downloadResourceNormalFile(rid);
    }

    // 获取指定资源文件详情
    @GetMapping("/v1/normal/file/info/{rid}")
    public DataRespond getResourceNormalInfo(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "rid必须为纯数字字段") Integer rid) {
        return resourceNormalService.getResourceNormalDetail(rid);
    }

    // 编辑指定资源文件信息
    @PutMapping("/v1/normal/file/info/{rid}")
    public MsgRespond editResourceNormalInfo(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "rid必须为纯数字字段") Integer rid, @Validated @ModelAttribute ResourceNormalReq req, @RequestHeader("username") String username, @RequestHeader("auth") String auth) {
        return resourceNormalService.editResourceNormalInfo(rid, req, username, auth);
    }

    // 删除指定资源文件
    @DeleteMapping("/v1/normal/file/{rid}/{uid}")
    public MsgRespond deleteResourceNormal(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段") Integer rid, @PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段") Integer uid, @RequestHeader("username") String username, @RequestHeader("auth") String auth) {
        return resourceNormalService.deleteResourceNormal(rid, uid, username, auth);
    }

    // 获取指定用户上传的资源文件列表
    @GetMapping("/v1/file/normal/up/{up_id}/{page_size}/{offset}")
    public DataRespond getResourceNormalList(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段") Integer up_id, @PathVariable @Min(value = 1, message = "page_size必须为大于1的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer page_size, @PathVariable @Min(value = 0, message = "offset必须为大于或等于0的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer offset) {
        return resourceNormalService.getResourceNormalListByUpId(up_id, page_size, offset);
    }

    // 获取全部上传资源文件列表
    @GetMapping("/v1/file/normal/list/{page_size}/{offset}")
    public DataRespond getResourceNormalList(@PathVariable @Min(value = 1, message = "page_size必须为大于1的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer page_size, @PathVariable @Min(value = 0, message = "offset必须为大于或等于0的整数") @Digits(integer = Integer.MAX_VALUE, fraction = 0) Integer offset) {
        return resourceNormalService.getAllResourceNormalList(page_size, offset);
    }

    // 模糊查询资源名获取结果
    @GetMapping("/v1/file/normal/list/keyword/{keyword}/{page_size}/{offset}")
    public DataRespond getResourceNormalListByKeyword(@PathVariable("keyword")
                                                      @NotBlank(message = "keyword不能为空")
                                                      String keyword,
                                                      @PathVariable("page_size")
                                                      @Min(value = 1, message = "page_size必须为大于1的整数")
                                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                                      Integer page_size, @PathVariable("offset")
                                                      @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                                      Integer offset,
                                                      @RequestParam(required = false) Integer deptId,
                                                      @RequestParam(required = false) Integer tagId) {
        return resourceNormalService.getNormalResourceByKeyword(deptId, tagId, keyword, page_size, offset);
    }

    // 上传教材资源
    @PostMapping("/v2/lesson")
    public MsgRespond uploadLessonResource(@Validated @ModelAttribute ResourceLessonReq req){
        if (Objects.isNull(req.getResource_file())) {
            return MsgRespond.fail("上传文件不能为空");
        }
        return resourceLessonService.uploadResourceLesson(req);
    }

    // 获取课程下的各章节资源列表
    @GetMapping("/v2/lesson/list/{lesson_id}")
    public DataRespond getResourceLessonList(@PathVariable Integer lesson_id){
        return resourceLessonService.getResourceLessonByLessonId(lesson_id);
    }


    // 重传教材资源
    @PutMapping("/v2/lesson")
    public MsgRespond reUploadLessonResource(@Validated @ModelAttribute ResourceLessonReq req){
        if (Objects.isNull(req.getResource_file())) {
            return MsgRespond.fail("上传文件不能为空");
        }
        return resourceLessonService.reUploadResourceLesson(req);
    }

    // 删除指定课程章节教材文件
    @DeleteMapping("/v2/lesson/chapter/{chapter_id}")
    public MsgRespond deleteOneLessonResource(@PathVariable Integer chapter_id){
        return resourceLessonService.deleteOneLessonResource(chapter_id);
    }

    // 删除指定课程下所有教材文件
    @PostMapping("/v2/lesson/{lesson_id}")
    public MsgRespond deleteAllLessonResource(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段") Integer lesson_id){
        return resourceLessonService.deleteAllLessonResource(lesson_id);
    }

    // 根据课程教材ID获取教材
    @GetMapping("/v1/lesson/load/{resource_lesson_id}")
    public ResponseEntity<?> getResourceLessonFile(@RequestHeader(name="Range", required = false) String rangeString,
                                                   @PathVariable Integer resource_lesson_id){
        return resourceLessonService.getResourceLessonById(rangeString, resource_lesson_id);
    }

    // 获取指定教材资源类型
    @GetMapping("/v2/lesson/type/{resource_id}")
    public DataRespond getResourceLessonType(@PathVariable Integer resource_id){
        return resourceLessonService.getResourceLessonType(resource_id);
    }

    // 获取指定教材资源ID
    @GetMapping("/v2/lesson/id/{lesson_id}/{chapter_id}")
    public DataRespond getResourceLessonId(@PathVariable("lesson_id")Integer lessonId,
                                           @PathVariable("chapter_id")Integer chapterId){
        return resourceLessonService.getResourceLessonId(lessonId, chapterId);
    }

    // 上传学习笔记
    @PostMapping("/v1/file/note")
    public DataRespond uploadNote(@Validated @ModelAttribute ResourceNoteReq req){
        if (Objects.isNull(req.getNote_file())){
            return new DataFailRespond("上传笔记不能为空");
        }
        return resourceNoteService.uploadNote(req);
    }

    // 根据笔记ID删除用户发布的笔记
    @DeleteMapping("/v1/file/note/{user_id}/{note_id}")
    public MsgRespond deleteOneNote(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段") Integer user_id,
                                    @PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "note_id必须为纯数字字段") Integer note_id,
                                    @RequestHeader(name = "username") String username,
                                    @RequestHeader(name = "auth") String auth){
        return resourceNoteService.deleteOneNoteByUser(user_id, note_id, username, auth);
    }

    // 根据课程和章节ID删除笔记（仅内部）
    @DeleteMapping("/v2/file/note/lesson/chapter/{lesson_id}/{chapter_id}")
    public MsgRespond deleteNoteByChapterId(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段") Integer lesson_id,
                                            @PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段") Integer chapter_id){
        return resourceNoteService.deleteNoteByChapter(lesson_id, chapter_id);
    }

    // 根据课程ID删除笔记（仅内部）
    @DeleteMapping("/v2/file/note/lesson/{lesson_id}")
    public MsgRespond deleteNoteByLessonId(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段") @NotNull(message = "lesson_id不能为空") Integer lesson_id){
        return resourceNoteService.deleteNoteByLesson(lesson_id);
    }

    // 根据笔记ID获取笔记
    @GetMapping("/v2/file/note/{note_id}")
    public ResponseEntity<?> getNote(@PathVariable @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "note_id必须为纯数字字段") @NotNull(message = "note_id不能为空") Integer note_id){
        return resourceNoteService.getNoteById(note_id);
    }



}
