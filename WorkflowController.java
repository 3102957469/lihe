package com.zx.workflow.controller;

import com.zx.workflow.comon.BeanUtils;
import com.zx.workflow.comon.InputVO;
import com.zx.workflow.comon.ResultBean;
import com.zx.workflow.comon.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author py
 * @program com.zx.workflow.controller
 * @description 流程引擎控制器
 * @create 2021-01-15 17:33
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("workflow")
public class WorkflowController {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @RequestMapping("bpmnImport")
    public Object bpmnImport(String name, String file) {
        Deployment deploy = repositoryService
                .createDeployment()
                .name(name)
                .addClasspathResource(file)
                .deploy();
        log.info("---" + deploy.getName());
        log.info("---" + deploy.getId());
        return deploy.getId() +"----"+deploy.getName()+"---"+deploy.getTenantId();
    }

    @RequestMapping("start")
    public Object start(@RequestBody InputVO inputVO) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(inputVO.getProcessKey(),inputVO.getVariable());  //画流程图时设置的process_id
        log.info("processInstance:" + processInstance);
        if (processInstance != null) {
            log.info("流程发起成功" + processInstance.getId() + "----" + processInstance.getBusinessKey());
        } else {
            log.info("流程发起失败");
        }
        return processInstance.getId()+"---"+processInstance.getProcessInstanceId()+"-----"+processInstance.getProcessDefinitionId();
    }

    @RequestMapping("complete")
    public void complete(@RequestBody InputVO inputVO) {
        taskService.complete(inputVO.getTaskId());
    }

    @RequestMapping("completeByMap")
    public void completeByMap(@RequestBody InputVO inputVO) {
        taskService.complete(inputVO.getTaskId(), inputVO.getVariable());
    }

    @RequestMapping("findTaskByProcessId")
    public Object findTaskByProcessId(String processId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).list();
        List list = BeanUtils.copyList(tasks, TaskVO.class);
        return list;
    }

    @RequestMapping("findTaskById")
    public Object findTaskById(String taskId) {
        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        List list = BeanUtils.copyList(tasks, TaskVO.class);
        return list;
    }

    @RequestMapping("findTaskByUserId")
    public Object findTaskByUserId(String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
        List list = BeanUtils.copyList(tasks, TaskVO.class);
        return list;
    }

    @RequestMapping("findTaskByUserAndTaskId")
    public Object findTaskByUserAndTaskId(String userId, String taskId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).taskId(taskId).list();
        List list = BeanUtils.copyList(tasks, TaskVO.class);
        return list;
    }

    @RequestMapping("findTaskByPid")
    public Object findTaskByPid(String instanceId, String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).processInstanceId(instanceId).list();
        List list = BeanUtils.copyList(tasks, TaskVO.class);
        return list;
    }

    /**
     * 查看任务信息
     */
    @RequestMapping(value = "show")
    @ResponseBody
    public Object show(String taskId) {
        Map<String, Object> processVariables = taskService.getVariables(taskId);
        System.out.println(processVariables);
        return processVariables;
    }

    @RequestMapping("/claimTest")
    public Object claimTest(String taskId, String userId) {
        taskService.claim(taskId, userId);
        return "123";
    }

    @RequestMapping(value = "testUserAndGroup")
    public Object testUserAndGroup() {
        Group group = identityService.newGroup("deptLeader");
        group.setName("部门领导");
        group.setType("assignment");
        identityService.saveGroup(group);

        //创建并保存用户对象
        User user = identityService.newUser("hello_");
        user.setFirstName("Hello");
        user.setLastName("_");
        user.setEmail("475591383@qq.com");
        identityService.saveUser(user);

        //将用户hello放入deptLeader中
        identityService.createMembership("hello_", "deptLeader");
        User userInGroup = identityService.createUserQuery().memberOfGroup("deptLeader").singleResult();
//        Assert.assertNotNull(userInGroup);
//        Assert.assertEquals("hello_", user.getId());
        //查询组信息
        Group groupContainsHello = identityService.createGroupQuery().groupMember("hello_").singleResult();
//        Assert.assertNotNull(groupContainsHello);
//        Assert.assertEquals("deptLeader", groupContainsHello.getId());
        return userInGroup;
    }

    @RequestMapping(value = "memberOfGroup")
    public Object memberOfGroup(String groupId) {
        List<User> userInGroup = identityService.createUserQuery()
                .memberOfGroup(groupId).list();
        return userInGroup;
    }

    @RequestMapping(value = "groupMember")
    public Object groupMember(String userId) {
        List<Group> userInGroup = identityService.createGroupQuery()
                .groupMember(userId).list();
        return userInGroup;
    }

    @RequestMapping(value = "history")
    public Object history(String processId) {
        //获取HistoryService实例
        HistoryService historyService = processEngine.getHistoryService();
        //添加查询条件
        List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery()
                        //选择特定实例
                        .processInstanceId(processId)
                        //选择已完成的
                        .finished()
                        //根据实例完成时间升序排列
                        .orderByHistoricActivityInstanceEndTime().asc()
                        .list();

        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityId() + " took "
                    + activity.getDurationInMillis() + " milliseconds");
        }
        return activities;
    }

    @RequestMapping(value = "isExist")
    public String isExist(String processId, String taskId, String userId) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (StringUtils.isNotBlank(processId)) taskQuery.processInstanceId(processId);
        if (StringUtils.isNotBlank(taskId)) taskQuery.taskId(taskId);
        if (StringUtils.isNotBlank(userId)) taskQuery.taskCandidateUser(userId);
        Task task = taskQuery.singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        return task.getId();
    }


    @RequestMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws IOException {
//        String processDefinitionId = "";
//        if (this.isFinished(processId)) {// 如果流程已经结束，则得到结束节点
//            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
//
//            processDefinitionId = pi.getProcessDefinitionId();
//        } else {// 如果流程没有结束，则取当前活动节点
//            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
//            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//            processDefinitionId = pi.getProcessDefinitionId();
//        }
//        List<String> highLightedActivitis = new ArrayList<String>();
//
//        /**
//         * 获得活动的节点
//         */
//        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();
//
//        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
//            String activityId = tempActivity.getActivityId();
//            highLightedActivitis.add(activityId);
//        }
//
//        List<String> flows = new ArrayList<>();
//        //获取流程图
//        BpmnModelInstance bpmnModel = repositoryService.getBpmnModelInstance(processDefinitionId);
//        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        InputStream in = repositoryService.getProcessDiagram(processId);
//        processDiagram
//        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
//        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "bmp", highLightedActivitis, flows, engconf.getActivityFontName(),
//                engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
//
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished()
                .processInstanceId(processInstanceId).count() > 0;
    }


    @RequestMapping("/histories")
    public ResultBean processHistories(String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        if (list != null && !list.isEmpty()) {
            return new ResultBean(HttpServletResponse.SC_OK, list);
        } else {
            return new ResultBean(HttpServletResponse.SC_NOT_FOUND, "未查询到流程历史");
        }
    }


    /**
     * 可用任务列表
     *
     * @param username 用户名称
     * @return ResultBean
     */
   // @ApiOperation("可用任务列表")
    @GetMapping("/available/tasks")
    public ResultBean<?> availableTaskList(@RequestParam("username") String username) {
        //1、根据用户名查询所在组
        List<Group> list = identityService.createGroupQuery().groupMember(username).list();
        if (list != null && !list.isEmpty()) {
            //2、获取所有组名
            //这里我配置错了，错把id当name了。
            List<String> collect = list.stream().map(Group::getId).collect(Collectors.toList());
            //3、根据组名查询所有任务
            List<Task> tasks = taskService.createTaskQuery().taskCandidateGroupIn(collect).list();
            if (tasks != null && !tasks.isEmpty()) {
                List<Map<String, Object>> resultList = new ArrayList<>(tasks.size());
                tasks.forEach(task -> {
                    Map<String, Object> map = new HashMap<>(3);
                    map.put("processDefinitionId", task.getProcessDefinitionId());
                    map.put("processInstanceId", task.getProcessInstanceId());
                    map.put("taskId", task.getId());
                    resultList.add(map);
                });
                return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", resultList);
            } else {
                return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "未查询到任务列表");
            }
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到所属组");
        }
    }

    /**
     * 任务审核
     *

     * @return ResultBean
     */
   // @ApiOperation("任务审核")
    @PostMapping("/review/task")
    public ResultBean<String> review( String taskId,String username) {
        //1、查询任务
        Task task = taskService.createTaskQuery().taskId(taskId)
                .taskAssignee(username).singleResult();
        if (task != null) {
            taskService.complete(taskId);
            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "任务审核成功");
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到任务");
        }
    }

//    public ActivitiHighLineDTO getHighlightNode(String processInsId) {
//        HistoricProcessInstance hisProIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInsId).singleResult();
//        //System.out.println(hisProIns.getProcessDefinitionName()+" "+hisProIns.getProcessDefinitionKey());
//        //===================已完成节点
//        List<HistoricActivityInstance> finished = historyService.createHistoricActivityInstanceQuery()
//                .processInstanceId(processInsId)
//                .finished()
//                .orderByHistoricActivityInstanceStartTime().asc()
//                .list();
//        Set<String> highPoint = new HashSet<>();
//        finished.forEach(t -> highPoint.add(t.getActivityId()));
//
//        //=================待完成节点
//        List<HistoricActivityInstance> unfinished = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInsId).unfinished().list();
//        Set<String> waitingToDo = new HashSet<>();
//        unfinished.forEach(t -> waitingToDo.add(t.getActivityId()));
//
//        //=================iDo 我执行过的
//        Set<String> iDo = new HashSet<>(); //存放 高亮 我的办理节点
//        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().taskAssignee(SecurityUtils.getUsername()).finished().processInstanceId(processInsId).list();
//        taskInstanceList.forEach(a -> iDo.add(a.getTaskDefinitionKey()));
//
//        //===========高亮线
//        Set<String> highLine2 = new HashSet<>(); //保存高亮的连线
//        //获取流程定义的bpmn模型
//        BpmnModelInstance bpmn = repositoryService.getBpmnModelInstance(hisProIns.getProcessDefinitionId());
//        //已完成任务列表 可直接使用上面写过的
//        List<HistoricActivityInstance> finishedList = historyService.createHistoricActivityInstanceQuery()
//                .processInstanceId(processInsId)
//                .finished()
//                .orderByHistoricActivityInstanceStartTime().asc()
//                .list();
//        int finishedNum = finishedList.size();
//        //循环 已完成的节点
//        for (int i = 0; i < finishedNum; i++) {
//            HistoricActivityInstance finItem = finishedList.get(i);
//            //根据 任务key 获取 bpmn元素
//            ModelElementInstance domElement = bpmn.getModelElementById(finItem.getActivityId());
//            //转换成 flowNode流程节点 才能获取到 输出线 和输入线
//            FlowNode act = (FlowNode)domElement;
//            Collection<SequenceFlow> outgoing = act.getOutgoing();
//            //循环当前节点的 向下分支
//            outgoing.forEach(v->{
//                String tarId = v.getTarget().getId();
//                //已完成
//                for (int j = 0; j < finishedNum; j++) {
//                    //循环历史完成节点 和当前完成节点的向下分支比对
//                    //如果当前完成任务 的结束时间 等于 下个任务的开始时间
//                    HistoricActivityInstance setpFinish = finishedList.get(j);
//                    String finxId = setpFinish.getActivityId();
//                    if(tarId.equals(finxId)){
//                        if(finItem.getEndTime().equals(setpFinish.getStartTime())){
//                            highLine2.add(v.getId());
//                        }
//                    }
//                }
//                //待完成
//                for (int j = 0; j < unfinished.size(); j++) {
//                    //循环待节点 和当前完成节点的向下分支比对
//                    HistoricActivityInstance setpUnFinish = unfinished.get(j);
//                    String finxId = setpUnFinish.getActivityId();
//                    if(tarId.equals(finxId)){
//                        if(finItem.getEndTime().equals(setpUnFinish.getStartTime())){
//                            highLine2.add(v.getId());
//                        }
//                    }
//                }
//
//            });
//        }
//
//        //返回结果
//        ActivitiHighLineDTO activitiHighLineDTO =new ActivitiHighLineDTO();
//        activitiHighLineDTO.setHighPoint(highPoint);
//        activitiHighLineDTO.setHighLine(highLine2);
//        activitiHighLineDTO.setWaitingToDo(waitingToDo);
//        activitiHighLineDTO.setiDo(iDo);
//        return activitiHighLineDTO;
//    }

}
