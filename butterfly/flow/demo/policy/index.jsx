'use strict';

import React, {Component} from 'react';
import './index.less';
import 'butterfly-dag/dist/index.css';
import { Canvas } from 'butterfly-dag';
import mockData from './data';
import {Button, Modal, Select, Slider,Input,Tree,Form} from "antd";
import Node from "./node";
import Axios from 'axios';
import Edge from "./edge";
import BaseEdge from "../industry/edge";
import BaseNode from "../entity-relationship/node";
import Modals from "antd/es/modal";

const mode = {
    'begin':{
        label : '开始',
        className : 'icon-background-color',
        nodeName: 'beginInvoke',
        eventName: 'beginInvoke',
        description : 'beginInvoke',
        Class:"Node",
        nodeType:"begin",
        color:"#c6e5ff",
        //endpoints:"[{id: 'right',orientation: [1, 0],pos: [0, 0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    },
    'end':{
        label : '结束',
        className : 'icon-background-color',
        nodeName: 'endInvoke',
        eventName: 'endInvoke',
        description : 'endInvoke',
        Class:"Node",
        nodeType:"end",
        color:"#c6e5ff",
        //endpoints:"endpoints: [{id:'left',orientation:[-1,0],pos:[0,0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    },
    'bean':{
        label : '绑定对象执行节点',
        className : 'icon-background-color',
        nodeName: 'beanInvoke',
        eventName: 'beanInvoke',
        description : 'beanInvoke',
        Class:"Node",
        nodeType:"bean",
        color:"#c6e5ff",
        //endpoints:"endpoints: [{id:'left',orientation:[-1,0],pos:[0,0.5]},{id:'right',orientation:[1,0],pos:[0,0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    },
    'method':{
        label : '本地方法执行节点',
        className : 'icon-background-color',
        nodeName: 'methodInvoke',
        eventName: 'methodInvoke',
        description : 'methodInvoke',
        Class:"Node",
        nodeType:"method",
        color:"#c6e5ff",
        //endpoints:"endpoints: [{id:'left',orientation:[-1,0],pos:[0,0.5]},{id:'right',orientation:[1,0],pos:[0,0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    },
    'service':{
        label : '远程调用执行节点',
        className : 'icon-background-color',
        nodeName: 'serverInvoke',
        eventName: 'serverInvoke',
        description : 'serverInvoke',
        Class:"Node",
        nodeType:"service",
        color:"#c6e5ff",
        //endpoints:"endpoints: [{id:'left',orientation:[-1,0],pos:[0,0.5]},{id:'right',orientation:[1,0],pos:[0,0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    },
    'condition':{
        text : '条件节点',
        className : 'icon-background-color',
        Class:"BaseNode",
        nodeType:"condition",
        nodeName: 'conditionInvoke',
        eventName: 'conditionInvoke',
        description : 'conditionInvoke',
        color:"#c6e5ff",
        //endpoints:"endpoints: [{id:'top',orientation:[0,-1],pos:[0.5,0]},{id:'bottom',orientation:[0,1],pos:[0.5,0]},{id:'left',orientation:[-1,0],pos:[0,0.5]},{id:'right',orientation:[1,0],pos:[0,0.5]}]",
        source : '',
        target : '',
        sourceNode : '',
        targetNode : '',
    }

}

const edges = {
    source : '',
    target : '',
    sourceNode : '',
    targetNode : '',
    arrow:true,
    type:'endpoint',
    arrowPosition:0.5,
    Class:'Edge'
}

class Policy extends Component {
    constructor(props) {
        super(props);

        // 节点信息
        this.allNode=[];
        // 连线信息
        this.allEdges=[];

        this.state = {
            // 开始节点启用状态
            addBeginNodesStatus: true,
            // 结束节点启用状态
            addEndNodesStatus: true,
            // 展示业务属性状态
            showNodeInfo: false,
            // 展示模态请求框
            showModalInfo: false,

            // 展示工作流组状态
            showSubFlowInfo: false,
            // 当前操作对象下标
            currentNodeIndex: -1,
            allNodes:[],
            allFlows:[],
            requertUrl:'',
            requertFlowId:'',
            requertInput:'',
            responseOutput:'',
            flowId:''
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleChange(event) {
        let name = event.target.name;
        this.allNode[this.state.currentNodeIndex][name] = event.target.value;
        this.setState({currentNodeIndex:this.state.currentNodeIndex});
        if (name == "label" || name == "text") {
            let oldText = this.canvas.getNode(this.allNode[this.state.currentNodeIndex]["id"]).dom.innerText;
            this.canvas.getNode(this.allNode[this.state.currentNodeIndex]["id"]).dom.innerHTML = this.canvas.getNode(this.allNode[this.state.currentNodeIndex]["id"]).dom.innerHTML.replace(oldText, event.target.value);
        }
    }

    handleInputChange(event) {
        let name = event.target.name;
        let value = event.target.value;
        if (name == 'url'){
            this.setState({requertUrl:value});
        }else if(name == 'flow' ) {
            this.setState({requertFlowId: value});
        }else if(name == 'input' ) {
            this.setState({requertInput: value});
        }
    }

    handleOk = e => {
        this.setState({
            showModalInfo: false,
        });
    };

    handleCancel = e => {
        this.setState({
            showModalInfo: false,
        });
    };

    getUrlParams(name, str) {
        const reg = new RegExp(`(^|&)${ name}=([^&]*)(&|$)`);
        const r = str.substr(1).match(reg);
        if (r != null) return  decodeURIComponent(r[2]); return '';
    }

    async componentDidMount() {
        let flowId = this.getUrlParams("flowId",this.props.location.search)
        this.setState({flowId:flowId});
        let mockData = await this.loadNodes(flowId);
        let mockAllFlows = await this.loadFlows();
        mockData.nodes && mockData.nodes.forEach(node => {
            if (node.Class === 'Node') {
                node.Class = Node;
            } else if (node.Class === 'BaseNode') {
                node.Class = BaseNode;
            }
        })
        mockData.edges && mockData.edges.forEach(edge => {
            if (edge.Class === 'Edge') {
                edge.Class = Edge;
            } else if (edge.Class === 'BaseEdge') {
                edge.Class = BaseEdge;
            }
        })
        let root = document.getElementById('dag-canvas');
        this.setState({allFlows:mockAllFlows});
        this.canvas = new Canvas({
            root: root,
            layout: 'ForceLayout',   //布局设置(可传)，可使用集成的，也可自定义布局
            zoomable: true,    // 可放大
            moveable: true,    // 可平移
            draggable: true,   // 可拖动
            disLinkable: true, // 可删除连线
            linkable: true,    // 可连线
            theme: {
                edge: {
                    type: 'Manhattan',  //线条默认类型：贝塞尔曲线，折线，直线，曼哈顿路由线，更美丽的贝塞尔曲线。分别为Bezier/Flow/Straight/Manhattan/AdvancedBezier
                    arrow: true,   //线条默认是否带箭头
                    isExpandWidth: false,//增加线条交互区域
                    defaultAnimate: true, //开启线条动画
                },
                endpoint:{
                    position: ['Top', 'Bottom', 'Left', 'Right'],
                }
            }
        });
        this.canvas.setMinimap(true);
        this.canvas.draw(mockData);
        let self = this;
        this.canvas.on('events', (data) => {
            // 断言节点添加
            if (data.type == "nodes:add"){
                data.nodes.forEach(function (node) {
                    let _node = Object.assign({...node.options}, mode[node.options.nodeType] ? {...mode[node.options.nodeType]} : {});
                    //let _node = Object.assign(mode[node.nodeType] ? {...mode[node.nodeType]} : {},{...node.options});
                    self.allNode.push(_node);
                });
            }

            // 断言画布点击事件
            if (data.type =="canvas:click"){
                this.setState({showNodeInfo: false})
            };

            // 断言节点点击事件
            if (data.type =="node:click"){
                let currentNode = null, selectIndex = -1;
                this.allNode.forEach(function(node, index){
                    if (node.id == data.node.options.id){
                        currentNode = node;
                        selectIndex = index;
                    }
                });
                this.setState({currentNodeIndex: selectIndex, showNodeInfo: true})

            }

            /*if(data.type == "drag:move"){
                let _return = false;
                if(!self.isSelfConnect(data.dragEndpoint.nodeId,data.dragEndpoint.id)){
                  _return = true;
                }

                if(_return){
                    console.log("禁止连线,"+data.dragEndpoint.nodeId +"_" +data.dragEndpoint.id+"相同锚点重复连线",data.dragEndpoint);
                    data.event = null;
                    return;
                }

            }*/

            // 断言连线事件
            if (data.type == "link:connect"){
                // 断言非初始化
                this.allNode.forEach(function(node){
                    data.links.forEach(function(line){
                        if (node.id == line.options.targetNode){
                            node.front = line.options.sourceNode;
                        }
                        if (node.id == line.options.sourceNode){
                            node.source = line.options.source != null ? line.options.source: line.options.sourceEndpoint;
                            node.target = line.options.target != null ? line.options.target: line.options.targetEndpoint;
                            node.sourceNode = line.options.sourceNode;
                            node.targetNode = line.options.targetNode;
                            node.next = line.options.targetNode;
                            let _edgs = Object.assign(edges ? {...edges} : {},{...node});
                            self.allEdges.push(_edgs);
                            // 设置连线动画
                            line.addAnimate();
                        }
                    });
                });
            }

            // 断言拖拽事件
            if (data.type == "drag:end"){
                let selectIndex = -1;

                let obj = data.dragNode != null ? data.dragNode : data.dragEndpoint;
                if (obj == null){
                    return;
                }
                this.allNode.forEach(function(node, index){
                    if (node.id == obj.id){
                        selectIndex = index;
                    }else if(node.id == obj.nodeId){
                        selectIndex = index;
                    }
                });
                this.allNode[selectIndex].left = obj.left != null ? obj.left:obj._left
                this.allNode[selectIndex].top = obj.top != null ? obj.top:obj._top
            }

            // 断言连线删除事件
            if(data.type == "links:delete"){
                console.info(this.canvas)
                let nodeId = data.links[0].options.nodeId;
                let _index;
                this.allEdges.forEach(function (edges,index) {
                    if (edges.nodeId == nodeId){
                        _index = index;
                    }
                })
                this.allEdges.splice(_index,1);
            }
        });
    }

    // 添加开始节点
    addBeginNodes = () => {
        if(!this.state.addBeginNodesStatus) {
            return;
        }
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode(
            {
                id: 'begin_'+random,
                label: '开始',
                className: 'icon-background-color',
                iconType: 'icon-bofang',
                top: 200+num,
                left: 477+num,
                flowId: this.state.flowId,
                nodeId: 'begin_'+random,
                Class: Node,
                nodeType:'begin',
                color: '#c6e5ff',
                front: 'root',

                endpoints: [{
                    id: 'right',
                    orientation: [1, 0],
                    pos: [0, 0.5]
                }]
            }
        );
        this.setState({
            addBeginNodesStatus: false
        });
    }

    // 添加bean对象执行节点
    addBeanServiceNodes = () => {
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'bean_'+random,
                label: '绑定对象执行节点',
                className: 'icon-background-color',
                flowId: this.state.flowId,
                nodeId: 'bean_'+random,
                iconType: 'icon-shujuji',
                top: 200+num,
                left: 677+num,
                nodeType:'bean',
                Class: Node,
                color: '#c6e5ff',
                convertRule:'',
                component:'',
                endpoints: [{id: 'left', orientation: [-1, 0], pos: [0, 0.5]},{id: 'right', orientation: [1, 0], pos: [0, 0.5]}],
            }
        );
    }

    // 添加本地方法执行节点
    addNativeServiceNodes = () => {

        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'method_'+random,
                label: '本地方法执行节点',
                className: 'icon-background-color',
                iconType: 'icon-guize-kai',
                top: 200+num,
                left: 677+num,
                nodeType:'method',
                flowId: this.state.flowId,
                nodeId: 'method_'+random,
                Class: Node,
                color: '#c6e5ff',
                component:'',
                url:'',
                methodName:'',
                inputParamTypesValues:'',
                endpoints: [{
                    id: 'left',
                    orientation: [-1, 0],
                    pos: [0, 0.5]
                },{
                    id: 'right',
                    orientation: [1, 0],
                    pos: [0, 0.5]
                }]
            }
        );

    }

    // 添加远程调用执行节点
    addRPCServiceNodes = () => {
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'rpc_'+random,
                label: '远程调用执行节点',
                className: 'icon-background-color',
                iconType: 'icon-guanlian',
                flowId: this.state.flowId,
                nodeId: 'rpc_'+random,
                top: 200+num,
                left: 677+num,
                nodeType:'service',
                Class: Node,
                color: '#c6e5ff',
                component:'',
                url:'',
                requertType:'',
                serverPort:'',
                methodName:'',
                inputParamTypesValues:'',
                timeout:'',
                eventType:'',
                output:'',
                endpoints: [{
                    id: 'left',
                    orientation: [-1, 0],
                    pos: [0, 0.5]
                },{
                    id: 'right',
                    orientation: [1, 0],
                    pos: [0, 0.5]
                }]
            }
        );
    }

    // 添加条件执行节点
    addConditionServiceNodes = () => {
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'condition_'+random,
                text: '条件节点',
                top: 200+num,
                left: 677+num,
                flowId: this.state.flowId,
                nodeId: 'condition_'+random,
                shape: 'diamond',
                className: 'icon-background-color',
                iconType: 'icon-rds',
                nodeType:'condition',
                Class: BaseNode,
                color: '#c6e5ff',
                component:'',
                endpoints: [{
                    id: 'top',
                    orientation: [0, -1],
                    pos: [0.5, 0]
                },{
                    id: 'bottom',
                    orientation: [0, 1],
                    pos: [0.5, 0]
                },{
                    id: 'left',
                    orientation: [-1, 0],
                    pos: [0, 0.5]
                },{
                    id: 'right',
                    orientation: [1, 0],
                    pos: [0, 0.5]
                }]
            }
        );
    }

    // 添加工作流组节点
    addSubFlowNode = (flowName,flowId) => {
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'subflow_'+random,
                label: flowName,
                className: 'icon-background-color',
                iconType: 'icon-sousuo',
                flowId: this.state.flowId,
                nodeId: 'subflow_'+random,
                top: 200+num,
                left: 677+num,
                nodeType:'subflow',
                Class: Node,
                color: '#86dad4',
                component:flowId,
                endpoints: [{id: 'left', orientation: [-1, 0], pos: [0, 0.5]},
                    {id: 'right', orientation: [1, 0], pos: [0, 0.5]}]
            }
        );
    }

    // 添加结束节点
    addEndNodes = () => {
        if(!this.state.addEndNodesStatus) {
            return;
        }
        let random = Math.ceil(Math.random() * 1000)
        let num = Math.ceil(Math.random() * 300)
        this.canvas.addNode({
                id: 'end_'+random,
                label: '结束',
                className: 'icon-background-color',
                iconType: 'icon-tingzhi',
                flowId: this.state.flowId,
                nodeId: 'end_'+random,
                top: 500,
                left: 677,
                nodeType:'end',
                Class: Node,
                color: '#c6e5ff',
                endpoints: [{
                    id: 'left',
                    orientation: [-1, 0],
                    pos: [0, 0.5]
                }]
            }
        );
        this.setState({
            addEndNodesStatus: false
        });
    }

    // 加载工作流
    loadNodes = async (flowId) => {
        let url = "http://127.0.0.1:8888/butterfly/get?flowId="+flowId;
        let _data = {};
        await Axios.post(url).
        then(response=>{
            if(response.data.status == 1){
                _data = response.data.data != null ? response.data.data : {};
            }else{
                alert(response.data.msg)}});
        return _data
    }

    // 加载工作流组
    loadFlows = async () =>{
        let url = "http://127.0.0.1:8888/butterfly/getFlows";
        let _data = {};
        await Axios.post(url).
        then(response=>{
            if(response.data.status == 1){
                _data = response.data.data != null ? response.data.data : {};
            }else{
                alert(response.data.msg)
            }});
        return _data;
    }

    // 保存工作流
    saveNodes = () => {
        let data ={};
        data.nodes = this.allNode;
        data.edges = this.allEdges;
        console.info("当前画布数据:"+this.canvas.nodes);
        let url = "http://127.0.0.1:8888/butterfly/save";
        Axios.post(url, data).then(response=>{ console.info(response)});
    }

    // 回退操作
    undoOP = () => {
        this.canvas.undo();
    }

    // 删除节点
    removeNodes = () => {
        console.info(this.allNode[this.state.currentNodeIndex]);
        if(this.allNode[this.state.currentNodeIndex] == null){
            alert("请选中节点");
            return;
        }
        let id = this.allNode[this.state.currentNodeIndex].id;
        this.canvas.removeNode(id);
        this.canvas.removeEdge({
            source:this.allNode[this.state.currentNodeIndex].sourceNode,
            target:this.allNode[this.state.currentNodeIndex].targetNode
        });
        this.allNode.splice(this.state.currentNodeIndex,1);
        let _index;
        this.allEdges.forEach(function (node,index) {
            console.info(node,id);
            if(node.targetNode == id){
                _index = index;
            }
        });

        this.allEdges.splice(_index,1);
    }

    // 打印工作流
    printFlow = () => {
        console.info("节点信息",this.allNode);
        console.info("连线信息",this.allEdges);
    }

    // 打开工作流请求模态框
    openFlowRequestModal = () =>{
        this.setState({showModalInfo:true});
        this.setState({requertUrl:"http://127.0.0.1:9999/"});
        this.setState({requertFlowId:this.state.flowId});
    }

    // 打开工作流请求模态框
    sendFlowRequest = async () =>{
        let url = this.state.requertUrl + "flow/" + this.state.requertFlowId;
        let _data = {};
        await Axios.post(url,JSON.parse(this.state.requertInput)).
        then(response=>{
            if(response.data.status == 1){
                this.setState({responseOutput:JSON.stringify(response.data)})
            }else{
                alert(response.data.msg)
            }});
        return _data;
    }

    // 模拟请求
    openSubFlow = (result) =>{
        this.setState({showSubFlowInfo:!result?true:false});
    }

    // 判断是否能连线
    isSelfConnect = (sourceNodeId,source) => {
        let _return = true;
        let _result = {};
        this.allEdges.forEach(function (edge,index) {
            if (edge.sourceNode == sourceNodeId && edge.source == source){
                _return = false;
                _result = edge;
            }
        });
        return _return;
    }

    render() {
        const {showNodeInfo, currentNodeIndex,showModalInfo,showSubFlowInfo} = this.state;
        return (
            <div className='policy-page'>
                <div className='operate-bar'>
                    <div className='operate-bar-title'>初始节点</div>
                    <div className='operate-item'>
                        <div className='operate-node'></div>
                        <Button onClick = {this.addBeginNodes}>开始节点</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.addEndNodes}>结束节点</Button>
                    </div>

                    <div className='operate-bar-title'>业务节点</div>
                    <div className='operate-item'>
                        <Button onClick = {this.addBeanServiceNodes}>绑定对象执行节点</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.addNativeServiceNodes}>本地方法执行节点</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.addRPCServiceNodes}>远程调用执行节点</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.addConditionServiceNodes}>条件执行节点</Button>
                    </div>

                    <div className='operate-bar-title'>子工作流节点 <span id="subflowState" onClick={ () =>this.openSubFlow(showSubFlowInfo)}><strong>&nbsp;&nbsp;&nbsp;{showSubFlowInfo?'-':'+'}</strong></span></div>
                    {showSubFlowInfo ?
                        this.state.allFlows.map(answer => <div className='operate-item' key={answer.flowName}><Button onClick = { () => this.addSubFlowNode(answer.flowName,answer.flowId) }>{answer.flowName}</Button></div>)
                        :<></>}
                    <div className='operate-bar-title'>制表操作</div>
                    <div className='operate-item'>
                        <Button onClick = {this.saveNodes}>保存操作</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.undoOP}>撤销操作</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.removeNodes}>删除节点操作</Button>
                    </div>
                    <div className='operate-item'>
                        <Button onClick = {this.printFlow}>打印工作流操作</Button>
                    </div>

                    <div className='operate-item'>
                        <Button onClick = {this.openFlowRequestModal}>模拟工作流请求</Button>
                    </div>
                </div>
                <div className='home-table'>
                    <Form >
                        <Modals title = "模拟工作流请求" visible={this.state.showModalInfo} onOk={this.sendFlowRequest} onCancel={this.handleCancel}>
                            <div className="content" style={{padding:'10px'}}>
                                <div className="info">
                                    请求地址: <Input name="url" style={{width:'70%'}} value={this.state.requertUrl} onChange={this.handleInputChange}/>
                                </div>
                            </div>
                            <div className="content" style={{padding:'10px'}}>
                                <div className="info">
                                    工作流ID: <Input name="flow" style={{width:'70%'}} value={this.state.requertFlowId} onChange={this.handleInputChange}/>
                                </div>
                            </div>
                            <div className="content" style={{padding:'10px'}}>
                                <div className="info" >
                                    请求参数: <textarea name="input" className="ant-input"  value={this.state.requertInput} style={{width:'70%'}} onChange={this.handleInputChange}/>
                                </div>
                            </div>
                            <div className="content" style={{padding:'10px'}}>
                                <div className="info">
                                    返回结果: <textarea name="output" className="ant-input"  value={this.state.responseOutput} style={{width:'70%'}}/>
                                </div>
                            </div>
                        </Modals>
                    </Form>
                </div>
                {
                    showNodeInfo ? <div className='operate-bar-prop'>
                        <div className='operate-bar-title'>节点基础属性</div>
                        <div className='operate-item'>
                            <div className='operate-node'>节点序号: <input name="id" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].id} onChange={this.handleChange} readOnly></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>节点名称: <input name={this.allNode[currentNodeIndex].shape === 'diamond' ? "text" : "label"} value={this.allNode[currentNodeIndex].shape === 'diamond' ? this.allNode[currentNodeIndex].text : this.allNode[currentNodeIndex].label} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>事件名称: <input name="eventName" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].eventName} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>节点描述: <input name="description" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].description} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>连线源头: <input name="source" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].source} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>连线目标: <input name="target" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].target} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>节点源头: <input name="sourceNode" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].sourceNode} onChange={this.handleChange}></input></div>
                        </div>
                        <div className='operate-item'>
                            <div className='operate-node'>节点目标: <input name="targetNode" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].targetNode} onChange={this.handleChange}></input></div>
                        </div>
                        {
                            this.renderNodePropMethod(currentNodeIndex)
                        }
                    </div> : <></>
                }
                <div className="policy-canvas" id="dag-canvas"/>
            </div>
        );
    }

    renderNodePropMethod(currentNodeIndex){
        if(this.allNode[currentNodeIndex].nodeType === "bean"){
            return (
                <>
                    <div className='operate-bar-title'>节点业务属性</div>
                    <div className='operate-item'>
                        <div className='operate-node'>执行类: <input name="component" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].component} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>JSON转换规则: <input name="convertRule" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].convertRule} onChange={this.handleChange}></input></div>
                    </div>
                </>
            );
        }else if(this.allNode[currentNodeIndex].nodeType === "subflow"){
            return (
                <>
                    <div className='operate-bar-title'>节点业务属性</div>
                    <div className='operate-item'>
                        <div className='operate-node'>执行类: <input name="component" readOnly value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].component} onChange={this.handleChange}></input></div>
                    </div>
                </>
            );
        }else if(this.allNode[currentNodeIndex].nodeType === "method"){
            return (
                <>
                    <div className='operate-bar-title'>节点业务属性</div>
                    <div className='operate-item'>
                        <div className='operate-node'>执行类: <input name="component" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].component} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求地址: <input name="url" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].url} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求方法: <input name="methodName" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].methodName} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求方法入参类型: <input name="inputParamTypesValues" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].inputParamTypesValues} onChange={this.handleChange}></input></div>
                    </div>
                </>
            );
        }else if(this.allNode[currentNodeIndex].nodeType === "condition"){
            return (
                <>
                    <div className='operate-bar-title'>节点业务属性</div>
                    <div className='operate-item'>
                        <div className='operate-node'>执行类: <input name="component" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].component} onChange={this.handleChange}></input></div>
                    </div>
                </>
            );
        }else if(this.allNode[currentNodeIndex].nodeType === "service"){
            return (
                <>
                    <div className='operate-bar-title'>节点业务属性</div>
                    <div className='operate-item'>
                        <div className='operate-node'>执行类: <input name="component" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].component} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求地址: <input name="url" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].url} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求协议: <input name="requertType" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].requertType} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求端口: <input name="serverPort" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].serverPort} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求方法: <input name="methodName" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].methodName} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求方法入参类型: <input name="inputParamTypesValues" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].inputParamTypesValues} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求超时时间: <input name="timeout" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].timeout} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>请求事件类型: <input name="eventType" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].eventType} onChange={this.handleChange}></input></div>
                    </div>
                    <div className='operate-item'>
                        <div className='operate-node'>输出参数结构: <input name="output" value={this.allNode[currentNodeIndex] === null ? '':this.allNode[currentNodeIndex].output} onChange={this.handleChange}></input></div>
                    </div>
                </>
            );
        }
    }
}

export default Policy;
