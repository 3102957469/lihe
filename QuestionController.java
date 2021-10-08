package springBootUEditorOSS.demo.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springBootUEditorOSS.demo.model.entity.JavaQuestion;
import springBootUEditorOSS.demo.model.request.JavaBatchQuestion;
import springBootUEditorOSS.demo.model.request.JavaQuestionReq;
import springBootUEditorOSS.demo.model.response.ResultRep;
import springBootUEditorOSS.demo.model.response.TopicVO;
import springBootUEditorOSS.demo.serviceImpl.JavaQuestionServiceImpl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    JavaQuestionServiceImpl service;

    @RequestMapping("/newInsert")
    public ResultRep<Object> newInsert(@RequestBody JavaBatchQuestion data) {
        try {
            for (JavaQuestion datum : data.getData()) {
                if (datum.getId() == 4320) {
                    System.out.println("---");
                }
                List<JavaQuestion> list = service.list(
                        new LambdaQueryWrapper<JavaQuestion>()
                                .eq(JavaQuestion::getQuestion, datum.getQuestion())
                                .eq(JavaQuestion::getContent, datum.getContent())
                );
                if (IterableUtils.size(list) > 0) {
                    continue;
                }
                try {
                    service.saveOrUpdate(datum);
                } catch (Exception e) {
                    log.error("---------------" + e.getMessage());
                }

            }
            return ResultRep.succeed("success");
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    @RequestMapping(path = "/page")
    public ResultRep<Object> page(JavaQuestionReq param) {
        //log.info("page request:{}", TJsonUtil.toJson(param));
        try {
            LambdaQueryWrapper<JavaQuestion> queryWrapper = new LambdaQueryWrapper<JavaQuestion>();
            queryWrapper.like(StringUtils.isNotBlank(param.getQuestion()), JavaQuestion::getQuestion, param.getQuestion());
            queryWrapper.like(StringUtils.isNotBlank(param.getSimplecontent()), JavaQuestion::getSimplecontent, param.getSimplecontent());
            queryWrapper.like(StringUtils.isNotBlank(param.getContent()), JavaQuestion::getContent, param.getContent());
            queryWrapper.like(StringUtils.isNotBlank(param.getType()), JavaQuestion::getType, param.getType());

            IPage<JavaQuestion> page = service.page(new Page<JavaQuestion>(param.getPageNum(), param.getPageSize()), queryWrapper);
            return ResultRep.succeed(page);
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    @RequestMapping(path = "/list")
    public ResultRep<Object> list(JavaQuestionReq param) {
        //log.info("list request:{}", TJsonUtil.toJson(param));
        try {
            LambdaQueryWrapper<JavaQuestion> queryWrapper = new LambdaQueryWrapper<JavaQuestion>();
            queryWrapper.like(StringUtils.isNotBlank(param.getQuestion()), JavaQuestion::getQuestion, param.getQuestion());
            queryWrapper.like(StringUtils.isNotBlank(param.getSimplecontent()), JavaQuestion::getSimplecontent, param.getSimplecontent());
            queryWrapper.like(StringUtils.isNotBlank(param.getContent()), JavaQuestion::getContent, param.getContent());
            queryWrapper.like(StringUtils.isNotBlank(param.getType()), JavaQuestion::getType, param.getType());

            queryWrapper.orderByDesc(JavaQuestion::getCreateTime);

            List<JavaQuestion> list = service.list(queryWrapper);
            return ResultRep.succeed(list);
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    @RequestMapping("/insert")
    public ResultRep<Object> insert(@RequestBody JavaQuestion param) {
        //log.info("insert request:{}", TJsonUtil.toJson(param));
        try {
            //isExists(param);
            setTime(param.getId() == null, param);
            service.save(param);
            return ResultRep.succeed("success");
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    @RequestMapping("/update")
    public ResultRep<Object> update(@RequestBody JavaQuestion param) {
        //log.info("update request:{}", TJsonUtil.toJson(param));
        try {
            //isExists(param);
            setTime(param.getId() == null, param);
            service.updateById(param);
            return ResultRep.succeed("success");
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    @RequestMapping("/delete")
    public ResultRep<Object> delete(@RequestBody JavaQuestion param) {
        //log.info("delete request:{}", TJsonUtil.toJson(param));
        try {
            service.removeById(param);
            return ResultRep.succeed("success");
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    public void setTime(boolean condition, JavaQuestion param) {
        if (condition) {
            param.setCreateTime(new Date());
        }
        param.setUpdateTime(new Date());
    }

//    public void isExists(JavaQuestion param){
//        LambdaQueryWrapper<JavaQuestion> queryWrapper = new LambdaQueryWrapper<JavaQuestion>();
//        queryWrapper.eq(JavaQuestion::getStuName,param.getStuName());
//        if (param.getId() != null) {
//            queryWrapper.ne(JavaQuestion::getId, param.getId());
//        }
//        List<JavaQuestion> list = service.list(queryWrapper);
//        if(list !=null && list.size()>0) {
//            throw new BusinessException(300,"data is exist");
//        }
//    }

    @RequestMapping("/getTopic")
    public ResultRep<Object> getTopic(String question, int page) {
        try {
            List<String> list =new LinkedList<>();
            list.addAll(FileUtil.readLines(new File("D:\\topic.txt"), "UTF-8"));
            list.addAll(FileUtil.readLines(new File("D:\\lifeTopic.txt"), "UTF-8"));
            List<TopicVO> c = new LinkedList<>();
            List<String> contentList3 = new LinkedList<>();
            for (int i = 0; i < list.size(); i++) {
                if (StringUtils.contains(list.get(i), question)) {
                    TopicVO topicVO = new TopicVO();
                    topicVO.setLine(i + 1);
                    topicVO.setContent(getContent(list, i, 0, page));
                    c.add(topicVO);
                    contentList3.addAll(getContent(list, i, 0, page));
                    contentList3.add("-----------------------");
                }
            }
            return ResultRep.succeed(contentList3);
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }

    public List<String> getContent(List<String> list, int line, int pre, int after) {
        List<String> contentList = list.subList(line - pre, line + after);
        List<String> contentList2 = new LinkedList<>();
        for (String content : contentList) {
            if (StringUtils.isBlank(content)) continue;
            contentList2.add(content);
        }
        return contentList2;
    }

    @RequestMapping("/toTxt")
    public ResultRep<Object> toTxt(@RequestBody JavaBatchQuestion data) {
        try {
            StringBuilder str = new StringBuilder();
            for (JavaQuestion datum : data.getData()) {
                str
                        .append(datum.getQuestion())
                        .append("\n")
                        .append(datum.getContent())
                        .append("\n")
                        .append("------------------------------------------------------------------------------")
                        .append("\n");
            }
            FileUtil.writeBytes(str.toString().getBytes(StandardCharsets.UTF_8),
                    new File("D:\\lifeTopic.txt"));
            return ResultRep.succeed("success");
        } catch (Exception e) {
            return ResultRep.fail(400, e.getMessage());
        }
    }
}
