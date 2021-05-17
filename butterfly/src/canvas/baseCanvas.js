'use strict';

const $ = require('jquery');
const _ = require('lodash');
const domtoimage = require('dom-to-image');

import Canvas from "../interface/canvas";
import Node from '../node/baseNode';
import Edge from '../edge/baseEdge';
import Group from '../group/baseGroup';
import Endpoint from '../endpoint/baseEndpoint';
import Layout from '../utils/layout/layout';
import SelectCanvas from '../utils/selectCanvas';
// 画布和屏幕坐标地换算
import CoordinateService from '../utils/coordinate';
// scope的比较
import ScopeCompare from '../utils/scopeCompare';
// 网格模式
import GridService from '../utils/girdService';
// 辅助线模式
import GuidelineService from '../utils/guidelineService';
// 小地图模式
import Minimap from '../utils/minimap';
// 线段动画
import LinkAnimateUtil from '../utils/link_animate';


import './baseCanvas.less';

class BaseCanvas extends Canvas {
  constructor(options) {
    super(options);
    this.root = options.root;
    this.layout = options.layout; // layout部分也需要重新review
    this.zoomable = options.zoomable || false; // 可缩放
    this.moveable = options.moveable || false; // 可平移
    this.draggable = options.draggable || false; // 可拖动
    this.linkable = options.linkable || false; // 可连线
    this.disLinkable = options.disLinkable || false; // 可拆线

    this.theme = {
      group: {
        type: _.get(options, 'theme.group.type') || 'normal'
      },
      edge: {
        type: _.get(options, 'theme.edge.type') || 'Bezier',
        Class: _.get(options, 'theme.edge.Class') || Edge,
        arrow: _.get(options, 'theme.edge.arrow'),
        arrowPosition: _.get(options, 'theme.edge.arrowPosition'),
        arrowOffset: _.get(options, 'theme.edge.arrowOffset'),
        label: _.get(options, 'theme.edge.label'),
        isRepeat: _.get(options, 'theme.edge.isRepeat') || false,
        isLinkMyself: _.get(options, 'theme.edge.isLinkMyself') || false,
        isExpandWidth: _.get(options, 'theme.edge.isExpandWidth') || false,
        defaultAnimate: _.get(options, 'theme.edge.defaultAnimate') || false,
      },
      endpoint: {
        position: _.get(options, 'theme.endpoint.position'),
        linkableHighlight: _.get(options, 'theme.endpoint.linkableHighlight') || false,
        limitNum: _.get(options, 'theme.endpoint.limitNum'),
        expandArea: {
          left: _.get(options, 'theme.endpoint.expandArea.left') === undefined ? 10 : _.get(options, 'theme.endpoint.expandArea.left'),
          right: _.get(options, 'theme.endpoint.expandArea.right') === undefined ? 10 : _.get(options, 'theme.endpoint.expandArea.right'),
          top: _.get(options, 'theme.endpoint.expandArea.top') === undefined ? 10 : _.get(options, 'theme.endpoint.expandArea.top'),
          bottom: _.get(options, 'theme.endpoint.expandArea.bottom') === undefined ? 10 : _.get(options, 'theme.endpoint.expandArea.bottom'),
        },
      },
      zoomGap: _.get(options, 'theme.zoomGap') || 0.001,
      // 鼠标到达边缘画布自动移动
      autoFixCanvas: {
        enable: _.get(options, 'theme.autoFixCanvas.enable', false),
        autoMovePadding: _.get(options, 'theme.autoFixCanvas.autoMovePadding') || [20, 20, 20, 20] // 上，右，下，左
      },
      // 自动适配父级div大小
      autoResizeRootSize: _.get(options, 'theme.autoResizeRootSize', true)
    };

    // 贯穿所有对象的配置
    this.global = _.get(options, 'global', {
      isScopeStrict: _.get(options, 'global.isScopeStrict'), // 是否为scope的严格模式
      limitQueueLen: 5 // 默认操作队列只有5步
    });

    // 放大缩小和平移的数值
    this._zoomData = 1;
    this._moveData = [0, 0];
    this._zoomTimer = null;

    this.groups = [];
    this.nodes = [];
    this.edges = [];

    // 框选模式，需要重新考虑(默认单选)
    this.isSelectMode = false;
    this.selecContents = [];
    this.selecMode = 'include';
    this.selectItem = {
      nodes: [],
      edges: [],
      groups: [],
      endpoints: []
    };
    // 框选前需要纪录状态
    this._remarkZoom = undefined;
    this._remarkMove = undefined;

    this.svg = null;
    this.wrapper = null;
    this.canvasWrapper = null;
    // 加一层wrapper方便处理缩放，平移
    this._genWrapper();
    // 加一层svg画线条
    this._genSvgWrapper();
    // 加一层canvas方便处理辅助
    this._genCanvasWrapper();
    // 动画初始化
    LinkAnimateUtil.init(this.svg);

    // 统一处理画布拖动事件
    this._dragType = null;
    this._dragNode = null;
    this._dragEndpoint = null;
    this._dragEdges = [];
    this._dragGroup = null;

    // 初始化一些参数
    this._rootWidth = $(this.root).width();
    this._rootHeight = $(this.root).height();
    $(this.root).css('overflow', 'hidden');
    if($(this.root).css('position') === 'static') {
      $(this.root).css('position', 'relative');
    }

    // 节点,线段,节点组z-index值，顺序：节点 > 线段 > 节点组
    this._dragGroupZIndex = 50;
    this._dragNodeZIndex = 250;
    this._dragEdgeZindex = 499;
    this._isInitEdgeZIndex = false;

    // 检测节点拖动节点组的hover状态
    this._hoverGroupQueue = [];
    this._hoverGroupObj = undefined;
    this._hoverGroupTimer = undefined;

    // 网格布局
    this._gridService = new GridService({
      root: this.root,
      canvas: this
    });

    // 辅助线
    this._guidelineService = new GuidelineService({
      root: this.root,
      canvas: this
    });
    this._bgObjQueue = [];
    this._bgObj = undefined;
    this._bgTimer = undefined;

    // 坐标转换服务
    this._coordinateService = new CoordinateService({
      canvas: this,
      terOffsetX: $(this.root).offset().left,
      terOffsetY: $(this.root).offset().top,
      terWidth: $(this.root).width(),
      terHeight: $(this.root).height(),
      canOffsetX: this._moveData[0],
      canOffsetY: this._moveData[1],
      scale: this._zoomData
    });

    this._addEventListener();

    this._unionData = {
      __system: {
        nodes: [],
        edges: [],
        groups: [],
        endpoints: []
      }
    };

    this._NodeClass = Node;

    // undo & redo队列
    this.actionQueue = [];
    this.actionQueueIndex = -1;

    // 画布边缘
    this._autoMoveDir = [];
    this._autoMoveTimer = null;
  }

  //===============================
  //[ 画布渲染 ]
  //===============================
  draw(opts, callback) {
    const groups = opts.groups || [];
    const nodes = opts.nodes || [];
    const edges = opts.edges || [];

    // 自动布局需要重新review
    if (this.layout) {
      this._autoLayout({
        groups,
        nodes,
        edges
      });
    }

    let drawPromise = new Promise((resolve, reject) => {
      setTimeout(() => {
        // 生成groups
        this.addGroups(groups);
        resolve();
      });
    }).then(() => {
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          // 生成nodes
          this.addNodes(nodes);
          resolve();
        }, 10);
      });
    }).then((resolve) => {
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          // 生成edges
          this.addEdges(edges);
        resolve();
        }, 20);
      });
    });
    
    drawPromise.then(() => {
      this.actionQueue = [];
      this.actionQueueIndex = -1;
      callback && callback({
        nodes: this.nodes,
        edges: this.edges,
        groups: this.groups
      });
    });
  }
  redraw (opts, callback) {
    this.removeNodes(this.nodes.map((item) => item.id) || []);
    this.removeGroups(this.groups.map((item) => item.id) || []);
    this.clearActionQueue();
    this.draw(opts || {}, callback);
  }
  getDataMap() {
    return {
      nodes: this.nodes,
      edges: this.edges,
      groups: this.groups
    };
  }
  _genSvgWrapper() {
    function _detectMob() {
      const toMatch = [
          /Android/i,
          /webOS/i,
          /iPhone/i,
          /iPad/i,
          /iPod/i,
          /BlackBerry/i,
          /Windows Phone/i
      ];
      return toMatch.some((toMatchItem) => {
          return window.navigator.userAgent.match(toMatchItem);
      });
    }
    let _isMobi = _detectMob();
    let _SVGWidth = '100%';
    let _SVGHeight = '100%';
    
    let _detectZoom = () => {
      let ratio = 0;
      let screen = window.screen;
      let ua = window.navigator.userAgent.toLowerCase();

      if (window.devicePixelRatio !== undefined) {
        ratio = window.devicePixelRatio;
      }
      else if (~ua.indexOf('msie')) {
        if (screen.deviceXDPI && screen.logicalXDPI) {
          ratio = screen.deviceXDPI / screen.logicalXDPI;
        }
      }
      else if (window.outerWidth !== undefined && window.innerWidth !== undefined) {
        ratio = window.outerWidth / window.innerWidth;
      }

      if (ratio) {
        ratio = Math.round(ratio * 100);
      }
      return ratio;
    };

    // hack 适配浏览器的缩放比例
    if (!_isMobi) {
      let _sclae = 1 / (_detectZoom() / 200);
      _SVGWidth = (1 * _sclae) + 'px';
      _SVGHeight = (1 * _sclae) + 'px';
    }

    // 生成svg的wrapper
    const svg = $(document.createElementNS('http://www.w3.org/2000/svg', 'svg'))
      .attr('class', 'butterfly-svg')
      .attr('width', _SVGWidth)
      .attr('height', _SVGHeight)
      .attr('version', '1.1')
      .attr('xmlns', 'http://www.w3.org/2000/svg')
      .appendTo(this.wrapper);

    if(!_isMobi) {
      // hack 监听浏览器的缩放比例并适配
      window.onresize = () => {
        let _sclae = 1 / (_detectZoom() / 200);
        svg.attr('width', (1 * _sclae) + 'px').attr('height', (1 * _sclae) + 'px');
      }

      // hack 因为width和height为1的时候会有偏移
      let wrapperOffset = $(this.wrapper)[0].getBoundingClientRect();
      let svgOffset = svg[0].getBoundingClientRect();
      svg.css('top', (wrapperOffset.top - svgOffset.top) + 'px').css('left', (wrapperOffset.left - svgOffset.left) + 'px');
    }

    return this.svg = svg;
  }
  _genWrapper() {
    // 生成wrapper
    const wrapper = $('<div class="butterfly-wrapper"></div>')
      .appendTo(this.root);
    return this.wrapper = wrapper[0];
  }
  _genCanvasWrapper() {
    // 生成canvas wrapper
    this.canvasWrapper = new SelectCanvas();
    this.canvasWrapper.init({
      root: this.root,
      _on: this.on.bind(this),
      _emit: this.emit.bind(this)
    });
  }
  _addEventListener() {
    if (this.zoomable) {
      this.setZoomable(true);
    }
    if (this.moveable) {
      this.setMoveable(true);
    }

    let _isChrome = /Chrome/.test(window.navigator.userAgent) && /Google Inc/.test(window.navigator.vendor);
    let _getChromeVersion = () => {
      var raw = window.navigator.userAgent.match(/Chrom(e|ium)\/([0-9]+)\./);
      return raw ? parseInt(raw[2], 10) : false;
    };
    let _isHightVerChrome = _isChrome && _getChromeVersion() >= 64;

    // todo：chrome64版本ResizeObserver对象不存在，但官方文档说64支持，所以加强判断下
    if (_isHightVerChrome && window.ResizeObserver && this.theme.autoResizeRootSize) {
      // 监听某个dom的resize事件
      const _resizeObserver = new ResizeObserver(entries => {
        this._rootWidth = $(this.root).width();
        this._rootHeight = $(this.root).height();
        this._coordinateService._changeCanvasInfo({
          terOffsetX: $(this.root).offset().left,
          terOffsetY: $(this.root).offset().top,
          terWidth: $(this.root).width(),
          terHeight: $(this.root).height()
        });
        this.canvasWrapper.resize({root: this.root});
        this.setGirdMode(true, undefined , true);
      });
      _resizeObserver.observe(this.root);
    } else {
      //  降级处理，监控窗口的resize事件
      window.addEventListener('resize', () => {
        this._rootWidth = $(this.root).width();
        this._rootHeight = $(this.root).height();
        this._coordinateService._changeCanvasInfo({
          terOffsetX: $(this.root).offset().left,
          terOffsetY: $(this.root).offset().top,
          terWidth: $(this.root).width(),
          terHeight: $(this.root).height()
        });
        this.canvasWrapper.resize({root: this.root});
        this.setGirdMode(true, undefined, true);
      })
    }

    // 绑定一大堆事件，group:addMember，groupDragStop，group:removeMember，beforeDetach，connection，
    this.on('InnerEvents', (data) => {
      if (data.type === 'node:addEndpoint') {
        this._addEndpoint(data.data, 'node', data.isInited);
      } else if (data.type === 'node:removeEndpoint') {
        let _point = data.data;
        let rmEdges = this.edges.filter((item) => {
          return (item.sourceNode.id === _point.nodeId && item.sourceEndpoint.id === _point.id) ||
                 (item.targetNode.id === _point.nodeId && item.targetEndpoint.id === _point.id);
        });
        this.removeEdges(rmEdges);
      } else if (data.type === 'group:addEndpoint') {
        this._addEndpoint(data.data, 'group', data.isInited);
      } else if (data.type === 'node:dragBegin') {
        this._dragType = 'node:drag';
        this._dragNode = data.data;
      } else if (data.type === 'node:mouseDown') {
        this._dragType = 'node:mouseDown';
      } else if (data.type === 'group:dragBegin') {
        this._dragType = 'group:drag';
        this._dragNode = data.data;
      } else if (data.type === 'endpoint:drag') {
        this._dragType = 'endpoint:drag';
        this._dragEndpoint = data.data;
      } else if (data.type === 'node:move') {
        this._moveNode(data.node, data.x, data.y, data.isNotEventEmit);
      } else if (data.type === 'group:move') {
        this._moveGroup(data.group, data.x, data.y, data.isNotEventEmit);
      } else if (data.type === 'link:click') {
        this._dragType = 'link:click';
      } else if (data.type === 'multiple:select') {
        const result = this._selectMultiplyItem(data.range, data.toDirection);
        // 把框选的加到union的数组
        _.assign(this._unionData['__system'], this.selectItem);

        this.emit('system.multiple.select', {
          data: result
        });
        this.emit('events', {
          type: 'multiple:select',
          data: result
        });

        this.selectItem = {
          nodes: [],
          edges: [],
          endpoints: []
        };
      } else if (data.type === 'group:resize') {
        this._dragType = 'group:resize';
        this._dragGroup = data.group;
      } else if (data.type === 'node:delete') {
        this.removeNode(data.data.id);
      } else if (data.type === 'edge:delete') {
        this.removeEdge(data.data);
      } else if (data.type === 'group:delete') {
        this.removeGroup(data.data.id);
      } else if (data.type === 'group:addNodes') {
        _.get(data, 'nodes', []).forEach((item) => {
          let _hasNode = _.find(this.nodes, (_node) => {
            return item.id === _node.id;
          });
          if (!_hasNode) {
            this.addNode(item);
          } else {
            let neighborEdges = [];
            let rmItem = this.removeNode(item.id, true, true);
            let rmNode = rmItem.nodes[0];
            let _group = data.group;
            neighborEdges = rmItem.edges;
            rmNode._init({
              top: item.top - _group.top,
              left: item.left - _group.left,
              dom: rmNode.dom,
              group: _group.id
            });
            this.addNode(rmNode, true);
          }
        });
        if (!data.isNotEventEmit) {
          this.emit('events', {
            type: 'system.group.addMembers',
            nodes: data.nodes,
            group: data.group
          });
          this.emit('system.group.addMembers', {
            nodes: data.nodes,
            group: data.group
          });
        }
      } else if (data.type === 'group:removeNodes') {
        let _group = data.group;
        _.get(data, 'nodes', []).forEach((item) => {
          this.removeNode(item.id, true, true);
          item._init({
            group: undefined,
            left: item.left + _group.left,
            top: item.top + _group.top,
            dom: item.dom,
            _isDeleteGroup: true
          });
          this.addNode(item, true);
        });
      } else if (data.type === 'edge:updateLabel') {
        let labelDom = data.data.labelDom;
        $(this.wrapper).append(labelDom);
      } else if (data.type === 'edge:setZIndex') {
        this.setEdgeZIndex([data.edge], data.index);
      }
    });

    // 绑定拖动事件
    this._attachMouseDownEvent();
  }
  _attachMouseDownEvent() {
    let canvasOriginPos = {
      x: 0,
      y: 0
    };
    let nodeOriginPos = {
      x: 0,
      y: 0
    };

    let _isActiveEndpoint = false;
    let _activeItems = [];
    // 把其他可连接的point高亮
    let _activeLinkableEndpoint = (target) => {
      if (_isActiveEndpoint) {
        return;
      }
      let _allPoints = this._getAllEndpoints();
      _allPoints.forEach((_point) => {
        if (target === _point || _point.type === 'source' || _point._tmpType === 'source') {
          return;
        }
        if (_point.canLink && _point.canLink(target)) {
          if (_point.linkable) {
            _point.linkable();
            _point._linkable = true;
          }
          return;
        }
        if (ScopeCompare(target.scope, _point.scope)) {
          if (_point.linkable) {
            _point.linkable();
            _point._linkable = true;
          }
          _activeItems.push(_point);
          return;
        }
      });
      _isActiveEndpoint = true;
    };
    // 把其他可连接的point取消高亮
    let _unActiveLinkableEndpoint = () => {
      _isActiveEndpoint = false;
      _activeItems.forEach((item) => {
        item.unLinkable && item.unLinkable();
        item.unHoverLinkable && item.unHoverLinkable();
        item._linkable = false;
      });
      _activeItems = [];
    };


    let _oldFocusPoint = null;
    let _isFocusing = false;
    // 检查鼠标是否在可连的锚点上
    let _focusLinkableEndpoint = (cx, cy) => {
      if (!_isFocusing) {
        // if (_focusPoint) {
        //   _focusPoint.unHoverLinkable && _focusPoint.unHoverLinkable();
        //   _focusPoint = null;
        // }
        _isFocusing = true;
        setTimeout(() => {
          let _points = this._getAllEndpoints();
          let x = this._coordinateService._terminal2canvas('x', cx);
          let y = this._coordinateService._terminal2canvas('y', cy);
          let _focusPoint = null;
          _points.forEach((_point) => {
            const _maxX = _point._posLeft + _point._width + (_.get(_point, 'expandArea.right') || this.theme.endpoint.expandArea.right);
            const _maxY = _point._posTop + _point._height + (_.get(_point, 'expandArea.bottom') || this.theme.endpoint.expandArea.bottom);
            const _minX = _point._posLeft - (_.get(_point, 'expandArea.left') || this.theme.endpoint.expandArea.left);
            const _minY = _point._posTop - (_.get(_point, 'expandArea.top') || this.theme.endpoint.expandArea.top);
            if (x > _minX && x < _maxX && y > _minY && y < _maxY) {
              _focusPoint = _point;
            }
          });
          if (_focusPoint) {
            if (_focusPoint !== _oldFocusPoint) {
              _focusPoint.hoverLinkable && _focusPoint.hoverLinkable();
              _oldFocusPoint = _focusPoint;
            }
          } else {
            if (_oldFocusPoint) {
              _oldFocusPoint.unHoverLinkable && _oldFocusPoint.unHoverLinkable();
              _oldFocusPoint = null;
            }
          }
          _isFocusing = false;
        }, 100);
      }
    };

    const _clearDraging = () => {
      this._dragType = null;
      this._dragNode = null;
      this._dragEndpoint = null;
      this._dragGroup = null;
      this._dragEdges = [];
      nodeOriginPos = {
        x: 0,
        y: 0
      };
      canvasOriginPos = {
        x: 0,
        y: 0
      };
      this._autoMoveDir = [];
      this._guidelineService.isActive && this._guidelineService.clearCanvas();
    };

    const mouseDownEvent = (event) => {
      const LEFT_BUTTON = 0;
      if (event.button !== LEFT_BUTTON) {
        return;
      }

      if (!this._dragType && this.moveable) {
        this._dragType = 'canvas:drag';
      }

      // 假如点击在空白地方且在框选模式下
      if ((event.target === this.svg[0] || event.target === this.root) && this.isSelectMode) {
        this.canvasWrapper.active();
        this.canvasWrapper.dom.dispatchEvent(new MouseEvent('mousedown', {
          clientX: event.clientX,
          clientY: event.clientY
        }));
        return;
      }

      canvasOriginPos = {
        x: event.clientX,
        y: event.clientY
      };
      
      // 初始化z-index
      if (!this._isInitEdgeZIndex) {
        $(this.svg).css('z-index', this._dragEdgeZindex);
        this.nodes.forEach((item) => {
          $(item.dom).css('z-index', (this._dragNodeZIndex) * 2 - 1);
          _.get(item, 'endpoints').forEach((point) => {
            $(point.dom).css('z-index', this._dragNodeZIndex * 2);
          });
        });
        this.edges.forEach((item) => {
          if (item.labelDom) {
            $(item.labelDom).css('z-index', this._dragEdgeZindex + 1);
          }
        });
        this._isInitEdgeZIndex = true;
      }
      // 拖动的时候提高z-index
      if (this._dragNode && this._dragNode.__type == 'node') {
        $(this._dragNode.dom).css('z-index', (++this._dragNodeZIndex) * 2 - 1);
        _.get(this._dragNode, 'endpoints').forEach((point) => {
          $(point.dom).css('z-index', this._dragNodeZIndex * 2);
        });
      }
      if (this._dragNode && this._dragNode.__type == 'group') {
        $(this._dragNode.dom).css('z-index', (++this._dragGroupZIndex) * 2 - 1);
        _.get(this._dragNode, 'endpoints').forEach((point) => {
          $(point.dom).css('z-index', this._dragGroupZIndex * 2);
        });
      }
      
      this.emit('system.drag.start', {
        dragType: this._dragType,
        dragNode: this._dragNode,
        dragEndpoint: this._dragEndpoint,
        dragEdges: this._dragEdges,
        dragGroup: this._dragGroup,
        position: {
          clientX: event.clientX,
          clientY: event.clientY,
          canvasX: this._coordinateService._terminal2canvas('x', event.clientX),
          canvasY: this._coordinateService._terminal2canvas('y', event.clientY)
        }
      });
      this.emit('events', {
        type: 'drag:start',
        dragType: this._dragType,
        dragNode: this._dragNode,
        dragEndpoint: this._dragEndpoint,
        dragEdges: this._dragEdges,
        dragGroup: this._dragGroup,
        position: {
          clientX: event.clientX,
          clientY: event.clientY,
          canvasX: this._coordinateService._terminal2canvas('x', event.clientX),
          canvasY: this._coordinateService._terminal2canvas('y', event.clientY)
        }
      });
      this._autoMoveDir = [];
    };

    const mouseMoveEvent = (event) => {
      const LEFT_BUTTON = 0;
      if (event.button !== LEFT_BUTTON) {
        return;
      }
      if (this._dragType) {
        const canvasX = this._coordinateService._terminal2canvas('x', event.clientX);
        const canvasY = this._coordinateService._terminal2canvas('y', event.clientY);
        const offsetX = event.clientX - canvasOriginPos.x;
        const offsetY = event.clientY - canvasOriginPos.y;
        if (this._dragType === 'canvas:drag') {
          this.move([offsetX + this._moveData[0], offsetY + this._moveData[1]]);
          canvasOriginPos = {
            x: event.clientX,
            y: event.clientY
          };
        } else if (this._dragType === 'node:drag') {
          if (nodeOriginPos.x === 0 && nodeOriginPos.y === 0) {
            nodeOriginPos = {
              x: canvasX,
              y: canvasY
            };
            return;
          }
          if (this._dragNode) {
            let moveNodes = [this._dragNode];
            const unionKeys = this._findUnion('nodes', this._dragNode);
            if (unionKeys && unionKeys.length > 0) {
              unionKeys.forEach((key) => {
                moveNodes = moveNodes.concat(this._unionData[key].nodes);
              });
              moveNodes = _.uniqBy(moveNodes, 'id');
            } else {
              this._rmSystemUnion();
            }
            $(this.svg).css('visibility', 'hidden');
            $(this.wrapper).css('visibility', 'hidden');
            moveNodes.forEach((node) => {
              this._moveNode(node, node.left + (canvasX - nodeOriginPos.x), node.top + (canvasY - nodeOriginPos.y));
              if (this._guidelineService.isActive) {
                this._guidelineService.draw(node, 'node');
              }
            });
            $(this.svg).css('visibility', 'visible');
            $(this.wrapper).css('visibility', 'visible');
            nodeOriginPos = {
              x: canvasX,
              y: canvasY
            };
            this._hoverGroup(this._dragNode);
            this.emit('system.node.move', {
              nodes: moveNodes
            });
            this.emit('events', {
              type: 'node:move',
              nodes: moveNodes
            });
            this._autoMoveCanvas(event.clientX, event.clientY, {
              type: 'node:drag',
              nodes: moveNodes
            }, (gap) => {
              nodeOriginPos.x += gap[0];
              nodeOriginPos.y += gap[1];
            });
          }
        } else if (this._dragType === 'group:drag') {
          if (nodeOriginPos.x === 0 && nodeOriginPos.y === 0) {
            nodeOriginPos = {
              x: canvasX,
              y: canvasY
            };
            return;
          }
          if (this._dragNode) {
            $(this.svg).css('visibility', 'hidden');
            $(this.wrapper).css('visibility', 'hidden');
            const group = this._dragNode;
            this._moveGroup(group, group.left + (canvasX - nodeOriginPos.x), group.top + (canvasY - nodeOriginPos.y));
            if (this._guidelineService.isActive) {
              this._guidelineService.draw(group, 'group');
            }
            $(this.svg).css('visibility', 'visible');
            $(this.wrapper).css('visibility', 'visible');
            nodeOriginPos = {
              x: canvasX,
              y: canvasY
            };
            this.emit('system.group.move', {
              group: group
            });
            this.emit('events', {
              type: 'group:move',
              group: group
            });
            this._autoMoveCanvas(event.clientX, event.clientY, {
              type: 'group:drag',
              group: group
            }, (gap) => {
              nodeOriginPos.x += gap[0];
              nodeOriginPos.y += gap[1];
            });
          }
        } else if (this._dragType === 'endpoint:drag') {
          const endX = this._coordinateService._terminal2canvas('x', event.clientX);
          const endY = this._coordinateService._terminal2canvas('y', event.clientY);

          // 明确标记source或者是没有type且没有线连上
          let _isSourceEndpoint = (this._dragEndpoint.type === 'source' || this._dragEndpoint.type === 'onlyConnect' || (!this._dragEndpoint.type && (!this._dragEndpoint._tmpType || this._dragEndpoint._tmpType === 'source')));
          let _isTargetEndpoint = (this._dragEndpoint.type === 'target' || (!this._dragEndpoint.type && this._dragEndpoint._tmpType === 'target')) && this._dragEndpoint.type !== 'onlyConnect';

          if (_isSourceEndpoint && this.linkable) {
            let unionKeys = this._findUnion('endpoints', this._dragEndpoint);

            let edges = [];
            if (!this._dragEdges || this._dragEdges.length === 0) {
              const EdgeClass = this.theme.edge.Class;
              let endpoints = [];
              if (unionKeys && unionKeys.length > 0) {
                unionKeys.forEach((key) => {
                  endpoints = endpoints.concat(this._unionData[key].endpoints);
                });
                endpoints = _.uniqBy(endpoints, (_point) => {return _point.nodeId + '||' + _point.id});
              } else {
                endpoints = [this._dragEndpoint];
              }
              endpoints.forEach((point) => {
                let _sourceNode = point.nodeType === 'node' ? this.getNode(point.nodeId) : this.getGroup(point.nodeId);
                let _sourceEndpoint = point;
                let pointObj = {
                  type: 'endpoint',
                  shapeType: this.theme.edge.type,
                  orientationLimit: this.theme.endpoint.position,
                  _sourceType: point.nodeType,
                  sourceNode: _sourceNode,
                  sourceEndpoint: _sourceEndpoint,
                  arrow: this.theme.edge.arrow,
                  arrowPosition: this.theme.edge.arrowPosition,
                  arrowOffset: this.theme.edge.arrowOffset,
                  label: this.theme.edge.label,
                  isExpandWidth: this.theme.edge.isExpandWidth
                };
                pointObj['options'] = _.assign({}, pointObj, {
                  sourceNode: _sourceNode.id,
                  sourceEndpoint: _sourceEndpoint.id
                });
                // 检查endpoint限制连接数目
                let _linkNums = this.edges.filter((_edge) => {
                  return _edge.sourceEndpoint.id === point.id;
                }).length + 1;
                if (_linkNums > point.limitNum) {
                  console.warn(`id为${point.id}的锚点限制了${point.limitNum}条连线`);
                  return;
                }
                let _newEdge = new EdgeClass(_.assign(pointObj, {
                  _global: this.global,
                  _on: this.on.bind(this),
                  _emit: this.emit.bind(this),
                }));
                _newEdge._init();
                $(this.svg).append(_newEdge.dom);
                if (_newEdge.labelDom) {
                  $(this.wrapper).append(_newEdge.labelDom);
                }
                if (_newEdge.arrowDom) {
                  $(this.svg).append(_newEdge.arrowDom);
                }
                edges.push(_newEdge);
              });
              this._dragEdges = edges;
            } else {
              edges = this._dragEdges;
            }

            $(this.svg).css('visibility', 'hidden');
            $(this.wrapper).css('visibility', 'hidden');
            let _targetPoint = {
              pos: [endX, endY],
            };
            edges.forEach((edge) => {
              let beginX = edge.sourceEndpoint._posLeft + edge.sourceEndpoint._width / 2;
              let beginY = edge.sourceEndpoint._posTop + edge.sourceEndpoint._height / 2;
              const _soucePoint = {
                pos: [beginX, beginY],
                orientation: edge.sourceEndpoint.orientation
              };
              edge.redraw(_soucePoint, _targetPoint);
            });
            $(this.svg).css('visibility', 'visible');
            $(this.wrapper).css('visibility', 'visible');

            if (this.theme.endpoint.linkableHighlight) {
              _activeLinkableEndpoint(this._dragEndpoint);
              _focusLinkableEndpoint(event.clientX, event.clientY);
            }

            this._autoMoveCanvas(event.clientX, event.clientY, {
              type: 'endpoint:drag',
              edges: edges
            }, (gap) => {
              edges.forEach((edge) => {
                let beginX = edge.sourceEndpoint._posLeft + edge.sourceEndpoint._width / 2;
                let beginY = edge.sourceEndpoint._posTop + edge.sourceEndpoint._height / 2;
                const _soucePoint = {
                  pos: [beginX, beginY],
                  orientation: edge.sourceEndpoint.orientation
                };
                _targetPoint.pos[0] += gap[0];
                _targetPoint.pos[1] += gap[1];
                edge.redraw(_soucePoint, _targetPoint);
              });
            });

            this.emit('system.drag.move', {
              dragType: this._dragType,
              pos: [event.clientX, event.clientY],
              dragNode: this._dragNode,
              dragEndpoint: this._dragEndpoint,
              dragEdges: edges
            });
            this.emit('events', {
              type: 'drag:move',
              dragType: this._dragType,
              pos: [event.clientX, event.clientY],
              dragNode: this._dragNode,
              dragEndpoint: this._dragEndpoint,
              dragEdges: edges
            });
          } else if (_isTargetEndpoint && this.disLinkable) {
            // 从后面搜索线
            let targetEdge = null;
            for (let i = this.edges.length - 1; i >= 0; i--) {
              if (this._dragEndpoint.id === _.get(this.edges, [i, 'targetEndpoint', 'id']) && this._dragEndpoint.nodeId === _.get(this.edges, [i, 'targetNode', 'id'])) {
                targetEdge = this.edges[i];
                break;
              }
            }
            if (targetEdge && this._dragEdges.length === 0) {
              targetEdge._isDeletingEdge = true;
              this._dragEdges = [targetEdge];
            }

            if (this._dragEdges.length !== 0) {
              let edge = this._dragEdges[0];
              let beginX = edge.sourceEndpoint._posLeft + edge.sourceEndpoint._width / 2;
              let beginY = edge.sourceEndpoint._posTop + edge.sourceEndpoint._height / 2;
              const _soucePoint = {
                pos: [beginX, beginY],
                orientation: edge.sourceEndpoint.orientation
              };
              const _targetPoint = {
                pos: [endX, endY],
              };
              edge.redraw(_soucePoint, _targetPoint);
            }
            if (this.theme.endpoint.linkableHighlight) {
              _activeLinkableEndpoint(this._dragEndpoint);
              _focusLinkableEndpoint(event.clientX, event.clientY);
            }
          }
        } else if (this._dragType === 'group:resize') {
          let canvasX = this._coordinateService._terminal2canvas('x', event.clientX);
          let canvasY = this._coordinateService._terminal2canvas('y', event.clientY);

          let _newWidth = canvasX - this._dragGroup.left;
          let _newHeight = canvasY - this._dragGroup.top;
          this._dragGroup.setSize(_newWidth, _newHeight);
        }
      }
    };

    const mouseEndEvent = (event) => {
      const LEFT_BUTTON = 0;
      if (event.button !== LEFT_BUTTON) {
        return;
      }

      let _unionNodes = [];

      _unActiveLinkableEndpoint();

      // 处理线条的问题
      if (this._dragType === 'endpoint:drag' && this._dragEdges && this._dragEdges.length !== 0) {
        // 释放对应画布上的x,y
        const x = this._coordinateService._terminal2canvas('x', event.clientX);
        const y = this._coordinateService._terminal2canvas('y', event.clientY);

        let _targetEndpoint = null;

        let _nodes = _.concat(this.nodes, this.groups);
        _nodes.forEach((_node) => {
          if (_node.endpoints) {
            _node.endpoints.forEach((_point) => {
              const _maxX = _point._posLeft + _point._width + (_.get(_point, 'expandArea.right') || this.theme.endpoint.expandArea.right);
              const _maxY = _point._posTop + _point._height + (_.get(_point, 'expandArea.bottom') || this.theme.endpoint.expandArea.bottom);
              const _minX = _point._posLeft - (_.get(_point, 'expandArea.left') || this.theme.endpoint.expandArea.left);
              const _minY = _point._posTop - (_.get(_point, 'expandArea.top') || this.theme.endpoint.expandArea.top);
              if (x > _minX && x < _maxX && y > _minY && y < _maxY) {
                _targetEndpoint = _point;
              }
            });
          }
        });

        let isDestoryEdges = false;
        // 找不到点 或者 目标节点不是target
        if (!_targetEndpoint || _targetEndpoint.type === 'source' || _targetEndpoint._tmpType === 'source') {
          isDestoryEdges = true;
        }

        // scope不同
        if (!isDestoryEdges) {
          isDestoryEdges = _.some(this._dragEdges, (edge) => {
            return !ScopeCompare(edge.sourceEndpoint.scope, _targetEndpoint.scope, _.get(this, 'global.isScopeStrict'));
          });
        }

        // 检查endpoint限制连接数目
        if (_targetEndpoint && _targetEndpoint.limitNum !== undefined) {
          let _linkNum = this.edges.filter((_edge) => {
            return _edge.targetEndpoint.id === _targetEndpoint.id;
          }).length + this._dragEdges.length;
          if (_linkNum > _targetEndpoint.limitNum) {
            console.warn(`id为${_targetEndpoint.id}的锚点限制了${_targetEndpoint.limitNum}条连线`);
            isDestoryEdges = true;
          }
        }

        if (isDestoryEdges) {
          this._dragEdges.forEach((edge) => {
            if (edge._isDeletingEdge) {
              this.removeEdge(edge);
            } else {
              edge.destroy(!edge._isDeletingEdge);
            }
          });
          // 把endpoint重新赋值
          this._dragEdges.forEach((_rmEdge) => {
            if (_.get(_rmEdge, 'sourceEndpoint._tmpType') === 'source') {
              let isExistEdge = _.some(this.edges, (edge) => {
                return _rmEdge.sourceNode.id === edge.sourceNode.id && _rmEdge.sourceEndpoint.id === edge.sourceEndpoint.id;
              });
              !isExistEdge && (_rmEdge.sourceEndpoint._tmpType = undefined);
            }
            if (_.get(_rmEdge, 'targetEndpoint._tmpType') === 'target') {
              let isExistEdge = _.some(this.edges, (edge) => {
                return _rmEdge.targetNode.id === edge.targetNode.id && _rmEdge.targetEndpoint.id === edge.targetEndpoint.id;
              });
              !isExistEdge && (_rmEdge.targetEndpoint._tmpType = undefined);
            }
          });
        } else {
          let _delEdges = [];
          let _reconnectInfo = [];

          let _emitEdges = this._dragEdges.filter((edge) => {
            // 线条去重
            if (!this.theme.edge.isRepeat) {
              let _isRepeat = _.some(this.edges, (_edge) => {
                let _result = false;
                if (edge.sourceNode) {
                  if (_edge.type === 'node') {
                    _result = edge.sourceNode.id === _edge.sourceNode.id;
                  } else {
                    _result = edge.sourceNode.id === _edge.sourceNode.id && edge.sourceEndpoint.id === _edge.sourceEndpoint.id;
                  }
                }

                if (_targetEndpoint.nodeId) {
                  if (_edge.type === 'node') {
                    _result = _result && (_.get(edge, 'targetNode.id') === _.get(_edge, 'targetNode.id'));
                  } else {
                    _result = _result && (_targetEndpoint.nodeId === _.get(_edge, 'targetNode.id') && _targetEndpoint.id === _.get(_edge, 'targetEndpoint.id'));
                  }
                }

                if (_result && edge._isDeletingEdge) {
                  _result = false;
                }

                return _result;
              });
              if (_isRepeat) {
                console.warn(`id为${edge.sourceEndpoint.id}-${_targetEndpoint.id}的线条连接重复，请检查`);
                edge.destroy();
                return false;
              }
            }
            let _preTargetNodeId = _.get(edge, 'targetNode.id');
            let _preTargetPointId = _.get(edge, 'targetEndpoint.id');
            let _currentTargetNode = _targetEndpoint.nodeType === 'node' ? this.getNode(_targetEndpoint.nodeId) : this.getGroup(_targetEndpoint.nodeId);
            let _currentTargetEndpoint = _targetEndpoint;
            if (_preTargetNodeId && _preTargetPointId && `${_preTargetNodeId}||${_preTargetPointId}` !== `${_currentTargetNode.id}||${_currentTargetEndpoint.id}`) {
              _delEdges.push(_.cloneDeep(edge));
              _reconnectInfo.push({
                edge,
                preTargetNodeId: _preTargetNodeId,
                preTargetPointId: _preTargetPointId,
                currentTargetNodeId: _currentTargetNode.id,
                currentTargetPointId: _currentTargetEndpoint.id
              });
              // source发生变化，target未变化
              edge.targetEndpoint.connectedNum -= 1;
              _targetEndpoint.connectedNum += 1;
            } else {
              // source和target都是新增
              edge.sourceEndpoint.connectedNum += 1;
              _targetEndpoint.connectedNum += 1;

            }
            edge._create({
              id: edge.id && !edge._isDeletingEdge ? edge.id : `${edge.sourceEndpoint.id}-${_targetEndpoint.id}`,
              targetNode: _currentTargetNode,
              _targetType: _targetEndpoint.nodeType,
              targetEndpoint: _currentTargetEndpoint,
              type: 'endpoint'
            });
            let _isConnect = edge.isConnect ? edge.isConnect() : true;
            if (!_isConnect) {
              console.warn(`id为${edge.sourceEndpoint.id}-${_targetEndpoint.id}的线条无法连接，请检查`);
              edge.destroy();
              return false;
            }

            // 正在删除的线重新连接
            if (edge._isDeletingEdge) {
              delete edge._isDeletingEdge;
            } else {
              edge.mounted && edge.mounted();
              this.edges.push(edge);
            }

            // 把endpoint重新赋值
            if (edge.type === 'endpoint' && !_.get(edge, 'sourceEndpoint.type') && !_.get(edge, 'sourceEndpoint._tmpType')) {
              edge.sourceEndpoint._tmpType = 'source';
            }
            if (edge.type === 'endpoint' && !_.get(edge, 'targetEndpoint.type') && !_.get(edge, 'targetEndpoint._tmpType')) {
              edge.targetEndpoint._tmpType = 'target';
            }

            return edge;
          });
          if (_delEdges.length !== 0 && _emitEdges.length !== 0) {
            this.pushActionQueue({
              type: 'system:reconnectEdges',
              data: {
                delLinks: _delEdges,
                addLinks: _emitEdges,
                info: _reconnectInfo
              }
            });
            this.emit('system.link.reconnect', {
              delLinks: _delEdges,
              addLinks: _emitEdges,
              info: _reconnectInfo
            });
            this.emit('events', {
              type: 'link:reconnect',
              delLinks: _delEdges,
              addLinks: _emitEdges,
              info: _reconnectInfo
            });
          } else {
            if (_delEdges.length !== 0) {
              _delEdges.forEach((_edge) => {
                this.pushActionQueue({
                  type: 'system:removeEdges',
                  data: _delEdges
                });
                this.emit('system.link.delete', {
                  link: _edge
                });
                this.emit('events', {
                  type: 'link:delete',
                  link: _edge
                });
              });
            }
            if (_emitEdges.length !== 0) {
              this.pushActionQueue({
                type: 'system:addEdges',
                data: this._dragEdges
              });
              this.emit('system.link.connect', {
                links: this._dragEdges
              });
              this.emit('events', {
                type: 'link:connect',
                links: this._dragEdges
              });
            }
          }
        }
      }
      if (this._dragType === 'node:drag' && this._dragNode) {

        let _handleDragNode = (dragNode) => {
          let sourceGroup = null;
          let targetGroup = null;
          let _nodeLeft = dragNode.left;
          let _nodeRight = dragNode.left + dragNode.getWidth();
          let _nodeTop = dragNode.top;
          let _nodeBottom = dragNode.top + dragNode.getHeight();

          if (dragNode.group) {
            const _group = this.getGroup(dragNode.group);
            const _groupLeft = _group.left;
            const _groupTop = _group.top;
            if (_nodeRight < 0 || _nodeLeft > _group.getWidth() || _nodeBottom < 0 || _nodeTop > _group.getHeight()) {
              _nodeLeft += _groupLeft;
              _nodeTop += _groupTop;
              _nodeRight += _groupLeft;
              _nodeBottom += _groupTop;
              sourceGroup = _group;
            } else {
              sourceGroup = _group;
              targetGroup = _group;
            }
          }

          if (!targetGroup) {
            targetGroup = this._findGroupByCoordinates(dragNode, _nodeLeft, _nodeTop, _nodeRight, _nodeBottom)
          }

          let neighborEdges = [];

          // 更新edge里面的字段，以防外面操作了里面dom
          let _updateNeighborEdge = (node, neighborEdges) => {
            neighborEdges.forEach((_edge) => {
              if (_edge.sourceNode.id === node.id) {
                _edge.sourceNode = node;
                let _sourceEndpoint = _.find(_edge.sourceNode.endpoints, (_point) => {
                  return _edge.sourceEndpoint.id === _point.id;
                });
                _edge.sourceEndpoint = _sourceEndpoint;
              }
              if (_edge.targetNode.id === node.id) {
                _edge.targetNode = node;
                let _targetEndpoint = _.find(_edge.targetNode.endpoints, (_point) => {
                  return _edge.targetEndpoint.id === _point.id;
                });
                _edge.targetEndpoint = _targetEndpoint;
              }
            });
          };
          this._hoverGroup(this._dragNode);
          if (sourceGroup) {
            // 从源组拖动到目标组
            if (sourceGroup !== targetGroup) {
              const rmItem = this.removeNode(dragNode.id, true, true);
              const rmNode = rmItem.nodes[0];
              neighborEdges = rmItem.edges;
              const nodeData = {
                id: rmNode.id,
                top: _nodeTop,
                left: _nodeLeft,
                dom: rmNode.dom,
                _isDeleteGroup: true
              };
              let step = this.actionQueue[this.actionQueueIndex];
              if (step.type === 'system:moveNodes') {
                step.data._isDraging = true;
              }
              this.pushActionQueue({
                type: 'system:groupRemoveMembers',
                data: {
                  group: sourceGroup,
                  nodes: [rmNode],
                  _isDraging: true
                }
              })
              this.emit('events', {
                type: 'system.group.removeMembers',
                group: sourceGroup,
                nodes: [rmNode]
              });
              this.emit('system.group.removeMembers', {
                group: sourceGroup,
                nodes: [rmNode]
              });

              if (targetGroup) {
                if (ScopeCompare(dragNode.scope, targetGroup.scope, _.get(this, 'global.isScopeStrict'))) {
                  nodeData.top -= targetGroup.top;
                  nodeData.left -= targetGroup.left;
                  nodeData.group = targetGroup.id;
                  nodeData._isDeleteGroup = false;
                  this.popActionQueue();
                  this.pushActionQueue({
                    type: 'system:groupAddMembers',
                    data: {
                      sourceGroup: sourceGroup,
                      targetGroup: targetGroup,
                      nodes: [rmNode],
                      _isDraging: true
                    }
                  });
                  this.emit('events', {
                    type: 'system.group.addMembers',
                    nodes: [rmNode],
                    group: targetGroup
                  });
                  this.emit('system.group.addMembers', {
                    nodes: [rmNode],
                    group: targetGroup
                  });
                  this._clearHoverGroup(targetGroup);
                } else {
                  console.warn(`nodeId为${dragNode.id}的节点和groupId${targetGroup.id}的节点组scope值不符，无法加入`);
                }
              }
              rmNode._init(nodeData);
              this.addNode(rmNode, true);
              _updateNeighborEdge(rmNode, neighborEdges);
            }
          } else {
            if (targetGroup) {
              if (ScopeCompare(dragNode.scope, targetGroup.scope, _.get(this, 'global.isScopeStrict'))) {
                const rmItem = this.removeNode(dragNode.id, true, true);
                const rmNode = rmItem.nodes[0];
                neighborEdges = rmItem.edges;
                rmNode._init({
                  top: _nodeTop - targetGroup.top,
                  left: _nodeLeft - targetGroup.left,
                  dom: rmNode.dom,
                  group: targetGroup.id
                });
                this.addNode(rmNode, true);
                let step = this.actionQueue[this.actionQueueIndex];
                if (step.type === 'system:moveNodes') {
                  step.data._isDraging = true;
                }
                _updateNeighborEdge(rmNode, neighborEdges);
                this.pushActionQueue({
                  type: 'system:groupAddMembers',
                  data: {
                    sourceGroup: undefined,
                    targetGroup: targetGroup,
                    nodes: [rmNode],
                    _isDraging: true
                  }
                });
                this.emit('events', {
                  type: 'system.group.addMembers',
                  nodes: [rmNode],
                  group: targetGroup
                });
                this.emit('system.group.addMembers', {
                  nodes: [rmNode],
                  group: targetGroup
                });
                this._clearHoverGroup(targetGroup);
              } else {
                console.warn(`nodeId为${dragNode.id}的节点和groupId${targetGroup.id}的节点组scope值不符，无法加入`);
              }
            }
          }
          neighborEdges.forEach((item) => {
            item.redraw();
          });
          dragNode.endpoints.forEach((item) => {
            item.updatePos && item.updatePos();
          });
          dragNode._isMoving = false;
        }
        let moveNodes = [this._dragNode];
        const unionKeys = this._findUnion('nodes', this._dragNode);
        if (unionKeys && unionKeys.length > 0) {
          unionKeys.forEach((key) => {
            moveNodes = moveNodes.concat(this._unionData[key].nodes);
          });
          moveNodes = _.uniqBy(moveNodes, 'id');
        }
        moveNodes.forEach((dragNode) => {
          _handleDragNode(dragNode);
        });
        _unionNodes = moveNodes;
        this._rmSystemUnion();
      }

      // 节点组放大缩小
      if (this._dragType === 'group:resize' && this._dragGroup) {
        this.emit('events', {
          type: 'system.group.resize',
          group: this._dragGroup
        });
        this.emit('system.group.resize', {
          group: this._dragGroup
        });
      }

      // 点击空白处触发canvas click，并且框选模式下不触发
      if ((this._dragType === 'canvas:drag' || !this._dragType) && !this.isSelectMode) {
        this.emit('system.canvas.click');
        this.emit('events', {
          type: 'canvas:click'
        });
      }

      if (this._dragType === 'node:drag' || this._dragType === 'group:drag') {
        this.pushActionQueue({
          type: '_system:dragNodeEnd'
        });
      }

      this.emit('system.drag.end', {
        dragType: this._dragType,
        dragNode: this._dragNode,
        dragEndpoint: this._dragEndpoint,
        dragEdges: this._dragEdges,
        dragGroup: this._dragGroup,
        unionNodes: _unionNodes
      });
      this.emit('events', {
        type: 'drag:end',
        dragType: this._dragType,
        dragNode: this._dragNode,
        dragEndpoint: this._dragEndpoint,
        dragEdges: this._dragEdges,
        dragGroup: this._dragGroup,
        unionNodes: _unionNodes
      });

      _clearDraging();
    };

    const mouseLeaveEvent = (event) => {
      if (this._dragEdges && this._dragEdges.length > 0) {
        this._dragEdges.forEach((edge) => {
          if (edge._isDeletingEdge) {
            this.removeEdge(edge);
          } else {
            edge.destroy(!edge._isDeletingEdge);
          }
        });
      }
      _clearDraging();
    }

    this.root.addEventListener('mousedown', mouseDownEvent);
    this.root.addEventListener('mousemove', mouseMoveEvent);
    // this.root.addEventListener('mouseleave', mouseEndEvent);
    this.root.addEventListener('mouseup', mouseEndEvent);
    this.root.addEventListener('mouseleave', (e) => {
      let toDom = e.toElement;
      if (!toDom) {
        return;
      }
      let toDomClassName = toDom.className;
      if (toDomClassName.indexOf('butterfly-tooltip') === -1) {
        mouseLeaveEvent();
      }
    });
  }



  //===============================
  //[ 节点渲染 ]
  //===============================
  getNode(id) {
    return _.find(this.nodes, item => item.id === id);
  }
  addNodes(nodes, isNotEventEmit) {
    const _canvasFragment = document.createDocumentFragment();
    const container = $(this.wrapper);
    const result = nodes.filter((node) => {
      if (node.group) {
        let _existGroup = this.getGroup(node.group);
        if (!_existGroup) {
          console.warn(`nodeId为${node.id}的节点找不到groupId为${node.group}的节点组，因此无法渲染`);
          return false;
        }
      }
      return true;
    }).map((node) => {
      let _nodeObj = null;
      if (node instanceof Node || node.__type === 'node') {
        _nodeObj = node;
      } else {
        const _NodeClass = node.Class || this._NodeClass;
        _nodeObj = new _NodeClass(_.assign(_.cloneDeep(node), {
          _global: this.global,
          _on: this.on.bind(this),
          _emit: this.emit.bind(this),
          _endpointLimitNum: this.theme.endpoint.limitNum,
          draggable: node.draggable !== undefined ? node.draggable : this.draggable
        }));
      }

      if (this._isExistNode(_nodeObj)) {
        // 后续用新的node代码旧的node
        console.warn(`node:${_nodeObj.id} has existed`);
        return;
      }

      // 节点初始化，假如已经存在过的节点就不需要重绘了
      let initObj = {};
      if (_nodeObj.dom) {
        initObj['dom'] = _nodeObj.dom
      }
      _nodeObj._init(initObj);
      // 一定要比group的addNode执行的之前，不然会重复把node加到this.nodes里面
      this.nodes.push(_nodeObj);

      // 假如节点存在group，即放进对应的节点组里
      const existGroup = _nodeObj.group ? this.getGroup(_nodeObj.group) : null;
      if (existGroup) {
        if (ScopeCompare(_nodeObj.scope, existGroup.scope, _.get(this, 'global.isScopeStrict'))) {
          existGroup._appendNodes([_nodeObj]);
        } else {
          console.warn(`nodeId为${_nodeObj.id}的节点和groupId${existGroup.id}的节点组scope值不符，无法加入`);
        }
      } else {
        _canvasFragment.appendChild(_nodeObj.dom);
      }
      return _nodeObj;
    }).filter((item) => {
      return !!item;
    });

    // 批量插入dom，性能优化
    container.append(_canvasFragment);

    result.forEach((item) => {
      // 渲染endpoint
      item._createEndpoint(isNotEventEmit);

      // 节点挂载
      !isNotEventEmit && item.mounted && item.mounted();
    });

    if (result && result.length > 0 && !isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:addNodes',
        data: result
      });
      this.emit('system.nodes.add', {
        nodes: result
      });
      this.emit('events', {
        type: 'nodes:add',
        nodes: result
      });
    }
    return result;
  }
  addNode(node, isNotEventEmit) {
    return this.addNodes([node], isNotEventEmit)[0];
  }
  removeNode(nodeId, isNotDelEdge, isNotEventEmit) {
    return this.removeNodes([nodeId], isNotDelEdge, isNotEventEmit);
  }
  removeNodes(nodes, isNotDelEdge, isNotEventEmit) {

    let nodeIds = [];
    nodeIds = nodes.map((item) => {
      if (item instanceof Node) {
        return item.id
      } else {
        return item;
      }
    });

    let rmNodes = [];
    let rmEdges = [];
    nodeIds.forEach((nodeId) => {
      const index = _.findIndex(this.nodes, _node => _node.id === nodeId);
      if (index === -1) {
        console.warn(`找不到id为：${nodeId}的节点`);
        return;
      }

      // 删除邻近的线条
      const neighborEdges = this.getNeighborEdges(nodeId);
      if (!isNotDelEdge) {
        this.removeEdges(neighborEdges, true, true);
      }

      // 删除节点
      const node = this.nodes[index];
      node.destroy(isNotEventEmit);

      const _rmNodes = this.nodes.splice(index, 1);
      // 假如在group里面
      if (node.group) {
        const group = this.getGroup(node.group);
        if (group) {
          group.nodes = group.nodes.filter(item => item.id !== node.id);
        }
      }

      if (_rmNodes.length > 0) {
        rmNodes = rmNodes.concat(_rmNodes);
        rmEdges = rmEdges.concat(neighborEdges);
      }

    });

    if (rmNodes.length > 0) {
      if (!isNotEventEmit) {
        this.pushActionQueue({
          type: 'system:removeNodes',
          data: {
            nodes: rmNodes,
            edges: rmEdges
          }
        });
        this.emit('system.nodes.delete', {
          nodes: rmNodes,
          edges: rmEdges
        });
        this.emit('events', {
          type: 'nodes:delete',
          nodes: rmNodes,
          edges: rmEdges
        });
      }
    }

    return {
      nodes: rmNodes,
      edges: rmEdges
    };
  }
  getNeighborNodes(nodeId) {
    const result = [];
    const node = _.find(this.nodes, item => nodeId === item.id);
    if (!node) {
      console.warn(`找不到id为${nodeId}的节点`);
    }
    this.edges.forEach((item) => {
      if (item.sourceNode && item.sourceNode.id === nodeId) {
        result.push(item.targetNode.id);
      } else if (item.targetNode && item.targetNode.id === nodeId) {
        result.push(item.sourceNode.id);
      }
    });

    return result.map(id => this.getNode(id));
  }
  /**
   * 查找 N 层节点
   * 
   * @param {Object} options 
   * @param {Node} options.node
   * @param {Endpoint} options.endpoint
   * @param {String} options.type
   * @param {Number} options.level
   * @param {Function} options.iteratee
   * @returns {Object} filteredGraph
   */
  getNeighborNodesAndEdgesByLevel({node, endpoint, type = 'out', level = Infinity, iteratee = () => true}) {
    // 先求source-target level 层
    if (!node || !this.nodes.length) return {nodes: [], edges: []};
    if (level == 0 || !this.edges.length) return {nodes: [node], edges: []};
    let quene = [];
    let neighbors = [];
    const visited = new Set();

    // 1. 生成邻接表
    // adjTable = {[nodeId]: {[endpointId]: [[targetNode, targetEndpoint]]}}
    const adjTable = this._getAdjcentTable(type);

    if (endpoint) {
      quene = _.get(adjTable, [node.id, endpoint.id], []).map(d => [...d, 1]);
    } else {
      quene = (Object.values(_.get(adjTable, node.id, {})) || []).flatMap(d => d.map(n => n.concat(1)));
    }

    visited.add(node.id);

    if (!quene.length) return {nodes: [node], edges: []};
    // 2. BFS,得到 nodes 集合
    while (quene.length) {
      const [$node, $endpoint, $level] = quene.shift();
      if (
        visited.has($node.id) ||
        $level > level ||
        !iteratee($node, $endpoint, $level)
      ) continue;

      // TODO: 锚点向后传递？节点向后传递？
      neighbors = (Object.values(_.get(adjTable, $node.id, {})) || []).forEach((neighborsWithEndpoint) => {
        neighborsWithEndpoint.forEach(neighbor => {
          const [$nNode, $nEndpoint] = neighbor;
          if (visited.has($nNode.id)) return;
          quene.push([...neighbor, $level + 1]);
        });
      });
      visited.add($node.id);
    }
    const nodes = new Set();
    const edges = new Set();
    // 4. 获取 edges，1. 只考虑点集 2. 考虑边
    // 目前只考虑点集内的全部边
    this.edges.forEach(edge => {
      const {sourceNode, sourceEndpoint, targetNode, targetEndpoint} = edge;
      if (visited.has(sourceNode.id) && visited.has(targetNode.id)) {
        nodes.add(sourceNode);
        nodes.add(targetNode);
        edges.add(edge);
      }
    });

    return {
      nodes: [...nodes],
      edges: [...edges]
    }
  }
  _getAdjcentTable(type) {
    // 包含正逆的邻接表
    // {[nodeId]: {[endpointId]: [[targetNode, targetEndpoint]]}}
    const adjTable = {};
    this.edges.forEach(edge => {
      const {sourceNode, sourceEndpoint, targetNode, targetEndpoint} = edge;
      const sourceNodeId = sourceNode.id;
      const sourceEndpointId = sourceEndpoint.id;
      const targetNodeId = targetNode.id;
      const targetEndpointId = targetEndpoint.id;
      // in and all
      if (type !== 'out') {
        if (!adjTable[targetNodeId]) adjTable[targetNodeId] = {};
        if (!adjTable[targetNodeId][targetEndpointId]) adjTable[targetNodeId][targetEndpointId] = [];

        adjTable[targetNodeId][targetEndpointId].push([sourceNode, sourceEndpoint]);
      }
      // out and all
      if (type !== 'in') {
        if (!adjTable[sourceNodeId]) adjTable[sourceNodeId] = {};
        if (!adjTable[sourceNodeId][sourceEndpointId]) adjTable[sourceNodeId][sourceEndpointId] = [];

        adjTable[sourceNodeId][sourceEndpointId].push([targetNode, targetEndpoint]);
      }

    });
    this.nodes.forEach(node => {
      if (!adjTable[node.id]) adjTable[node.id] = {};
    });
    return adjTable;
  }
  _isExistNode(node) {
    const hasNodes = this.nodes.filter(item => item.id === node.id);
    return hasNodes.length > 0;
  }
  _moveNode(node, x, y, isNotEventEmit) {
    
    let _isInGroup = !!node.group;
    if (_isInGroup) {
      let groupObj = this.getGroup(node.group);
      let groupType = groupObj.type || this.theme.group.type;
      if (groupType === 'inner') {
        let _groupW = $(groupObj.dom).width();
        let _groupH = $(groupObj.dom).height();
        let _nodeW = $(node.dom).width();
        let _nodeH = $(node.dom).height();
        if (x < 0 || (x + _nodeW) > _groupW || y < 0 || (y + _nodeH) > _groupH) {
          return ;
        }
      }
    }

    if (!isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:moveNodes',
        data: {
          node: node,
          top: y,
          left: x
        }
      });
    }

    // todo 防抖
    node._moveTo(x, y);
    node.endpoints && node.endpoints.forEach((item) => {
      item.updatePos();
    });
    this.edges.forEach((edge) => {
      if (edge.type === 'endpoint') {
        const isLink = _.find(node.endpoints, (point) => {
          return (point.nodeId === edge.sourceNode.id && !!edge.sourceNode.getEndpoint(point.id, 'source')) || (point.nodeId === edge.targetNode.id && !!edge.targetNode.getEndpoint(point.id, 'target'));
        });
        isLink && edge.redraw();
      } else if (edge.type === 'node') {
        const isLink = edge.sourceNode.id === node.id || edge.targetNode.id === node.id;
        isLink && edge.redraw();
      }
    });
  }
  _addEndpoint(endpoint, type, isInited) {
    let initOtps = {
      nodeType: type,
      _coordinateService: this._coordinateService
    };
    endpoint._init(initOtps);

    // 非自定义dom，自定义dom不需要定位
    if (!endpoint._isInitedDom) {
      const endpointDom = endpoint.dom;
      if (_.get(endpoint, '_node.group')) {
        const group = this.getGroup(endpoint._node.group);
        $(group.dom).append(endpointDom);
      } else {
        $(this.wrapper).prepend(endpointDom);
      }
      endpoint.updatePos();
    }
    endpoint.mounted && endpoint.mounted();
  }
  _getAllEndpoints() {
    let points = [];
    points = points.concat(this.nodes.map((_node) => {
      return _node.endpoints;
    }));
    points = points.concat(this.groups.map((_node) => {
      return _node.endpoints;
    }));
    points = points.filter((item) => {
      return !!item;
    });
    return _.flatten(points);
  }
  

  //===============================
  //[ 节点组渲染 ]
  //===============================
  getGroup(id) {
    return _.find(this.groups, item => item.id === id);
  }
  addGroup(group, nodes, options, isNotEventEmit) {
    const container = $(this.wrapper);
    const GroupClass = group.Class || Group;
    let _newNodes = [];
    const _groupObj = new GroupClass(_.assign(_.cloneDeep(group), {
      _global: this.global,
      _emit: this.emit.bind(this),
      _on: this.on.bind(this),
      draggable: group.draggable !== undefined ? group.draggable : this.draggable
    }));
    if (this._isExistGroup(_groupObj)) {
      // 后续用新的group代码旧的group
      console.warn(`group:${_groupObj.id} has existed`);
      return;
    }
    _groupObj.init();
    container.prepend(_groupObj.dom);
    this.groups.push(_groupObj);

    _groupObj._createEndpoint();

    _groupObj.mounted && _groupObj.mounted();

    if (nodes && nodes.length > 0) {
      // 过滤掉scope不匹配的节点 和 已存在其他组的node
      nodes = nodes.filter((_node) => {
        return ScopeCompare(_node.scope, _groupObj.scope, _.get(this, 'global.isScopeStrict')) && (_node.group === _groupObj.id || _node.group == undefined);
      });

      let _isAbsolutePos = _.get(options, 'posType', 'absolute') === 'absolute';
      // 重新计算group的位置
      let _groupLeft = Infinity;
      let _groupTop = Infinity;
      nodes.forEach((_node) => {
        if (_isAbsolutePos) {
          if (_node.left < _groupLeft) {
            _groupLeft = _node.left;
          }
          if (_node.top < _groupTop) {
            _groupTop = _node.top;
          }
        }
      });
      _groupObj._moveTo(_groupLeft - _.get(options, 'padding', 5), _groupTop - _.get(options, 'padding', 5));

      // 添加节点
      _newNodes = nodes.map((_node) => {
        let newNode = null;
        // 已存在节点
        let _existNode = _.find(this.nodes, (__node) => {
          return __node.id === _node.id;
        });
        if (_existNode) {
          this.removeNode(_existNode.id, true, true);
          _existNode._init({
            dom: _existNode.dom,
            top: _existNode.top - _groupObj.top,
            left: _existNode.left - _groupObj.left,
            group: _groupObj.id
          })
          newNode = this.addNode(_existNode, true);
        } else {
          let _nodeObj = null;
          if (_node instanceof Node || _node.__type === 'node') {
            _nodeObj = _node;
          } else {
            const _NodeClass = _node.Class || this._NodeClass;
            _nodeObj = new _NodeClass(_.assign(_.cloneDeep(_node), {
              _global: this.global,
              _on: this.on.bind(this),
              _emit: this.emit.bind(this),
              _endpointLimitNum: this.theme.endpoint.limitNum,
              draggable: _node.draggable !== undefined ? _node.draggable : this.draggable
            }));
          }
          if (_isAbsolutePos) {
            _nodeObj.top = _nodeObj.top - _groupObj.top;
            _nodeObj.left = _nodeObj.left - _groupObj.left;
          }
          _nodeObj.group = _groupObj.id;
          newNode = this.addNode(_nodeObj);
        }
        return newNode;
      });

      // 重新计算group的大小
      let _groupWidth = -Infinity;
      let _groupHeight = -Infinity;
      _newNodes.forEach((_node) => {
        let _w = $(_node.dom).width();
        let _h = $(_node.dom).height();
        if (_groupWidth < _node.left + _w) {
          _groupWidth = _node.left + _w;
        }
        if (_groupHeight < _node.top + _h) {
          _groupHeight = _node.top + _h;
        }
      });
      _groupObj.setSize(_groupWidth + _.get(options, 'padding', 5) * 2, _groupHeight + _.get(options, 'padding', 5) * 2);
    }

    if (!isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:addGroups',
        data: [{
          group: _groupObj,
          nodes: _newNodes
        }]
      });
      this.emit('events', {
        type: 'group:add',
        group: _groupObj
      });
    }

    return _groupObj;
  }
  addGroups(datas) {
    return datas.map(item => this.addGroup(item)).filter(item => item);
  }
  removeGroup(data, isNotEventEmit) {
    let groupId = undefined;
    if (data instanceof Group) {
      groupId = data.id;
    } else {
      groupId = data;
    }

    const group = this.getGroup(groupId);
    if (!group) {
      console.warn(`未找到id为${groupId}的节点组`);
    }
    group._isDeleting = true;
    let insideNodes = group.nodes.map((_node) => {
      let rmItem = this.removeNode(_node.id, true, true);
      let rmNode = rmItem.nodes[0];
      let neighborEdges = rmItem.edges;
      rmNode._init({
        top: _node.top + group.top,
        left: _node.left + group.left,
        dom: _node.dom,
        _isDeleteGroup: true
      });
      this.addNode(rmNode, true);
      neighborEdges.forEach((item) => {
        item.redraw();
      });
      return rmNode;
    });
    // 删除邻近的线条
    const neighborEdges = this.getNeighborEdges(group.id, 'group');
    this.removeEdges(neighborEdges, isNotEventEmit);
    // 删除group
    const index = _.findIndex(this.groups, _group => _group.id === groupId);
    this.groups.splice(index, 1)[0];
    group.destroy(isNotEventEmit);

    if (isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:removeGroup',
        data: {
          group: group,
          nodes: insideNodes
        }
      });
    }
    return group;
  }
  removeGroups(groups = [], isNotEventEmit) {
    let groupIds = [];
    groupIds = groups.map((item) => {
      if (item instanceof Group) {
        return item.id
      } else {
        return item;
      }
    });

    let rmGroups = groupIds.map((item) => {
      return this.removeGroup(item, isNotEventEmit);
    });

    return {
      groups: rmGroups
    }
  }
  _isExistGroup(group) {
    const hasGroups = this.groups.filter(item => item.id === group.id);
    return hasGroups.length > 0;
  }
  _moveGroup(group, x, y, isNotEventEmit) {
    if (!isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:moveGroups',
        data: {
          group: group,
          top: y,
          left: x
        }
      });
    }
    group._moveTo(x, y);
    this.edges.forEach((edge) => {
      let hasUpdate = _.get(edge, 'sourceNode.group') === group.id ||
        _.get(edge, 'targetNode.group') === group.id ||
        (_.get(edge, '_sourceType') === 'group' && _.get(edge, 'sourceNode.id') === group.id) ||
        (_.get(edge, '_targetType') === 'group' && _.get(edge, 'targetNode.id') === group.id);

      hasUpdate && (edge.redraw());
    });
  }
  // 拖动节点判断是否在节点组内
  _findGroupByCoordinates(node, lx, ty, rx, by) {
    for (let i = 0; i < this.groups.length; i++) {
      const _group = this.groups[i];
      const _groupLeft = _group.left;
      const _groupRight = _group.left + _group.getWidth();
      const _groupTop = _group.top;
      const _groupBottom = _group.top + _group.getHeight();
      let isInGroup = false;
      if (rx !== undefined && by !== undefined) {
        isInGroup = _groupLeft <= lx && _groupRight >= rx && _groupTop <= ty && _groupBottom >= by;
      } else {
        isInGroup = lx >= _groupLeft && lx <= _groupRight && ty >= _groupTop && ty <= _groupBottom;
      }
      if (isInGroup && _group.id !== node.group) {
        return _group;
      }
    }
  }
  // 拖动节点移动节点组高亮, 节流500ms检测一次
  _hoverGroup(node) {
    this._hoverGroupQueue.push(node);
    if (!this._hoverGroupTimer) {
      this._hoverGroupTimer = setInterval(() => {
        if (this._hoverGroupQueue.length === 0) {
          clearInterval(this._hoverGroupTimer);
          this._hoverGroupTimer = undefined;
          return;
        }
        let targetNode = this._hoverGroupQueue.pop();
        let targetGroup = this._findGroupByCoordinates(targetNode, targetNode.left, targetNode.top);
        if (targetGroup && targetGroup.scope && targetGroup.scope !== targetNode.scope) {
          return;
        }
        if (targetGroup) {
          if (!this._hoverGroupObj || targetGroup.id !== this._hoverGroupObj.id) {
            this._hoverGroupObj && $(this._hoverGroupObj.dom).removeClass('butterfly-group-hover');
            $(targetGroup.dom).addClass('butterfly-group-hover');
            this._hoverGroupObj = targetGroup;
          }
        } else {
          this._hoverGroupObj && $(this._hoverGroupObj.dom).removeClass('butterfly-group-hover');
          this._hoverGroupObj = undefined;
        }
        this._hoverGroupQueue = [];
      }, 200);
    }
  }
  _clearHoverGroup(group) {
    this._hoverGroupObj && $(this._hoverGroupObj.dom).removeClass('butterfly-group-hover');
    this._hoverGroupTimer = undefined;
    this._hoverGroupObj = undefined;
    this._hoverGroupQueue = [];
  }


  //===============================
  //[ 线段渲染 ]
  //===============================
  getEdge(id) {
    return _.find(this.edges, item => item.id === id);
  }
  addEdges(links, isNotEventEmit) {
    $(this.svg).css('visibility', 'hidden');

    const _edgeFragment = document.createDocumentFragment();
    const _labelFragment = document.createDocumentFragment();
    const result = links.map((link) => {

      // link已经存在
      if (link instanceof Edge) {
        link._init();

        _edgeFragment.appendChild(link.dom);

        if (link.labelDom) {
          _labelFragment.appendChild(link.labelDom);
        }

        if (link.arrowDom) {
          _edgeFragment.appendChild(link.arrowDom);
        }

        this.edges.push(link);

        link.mounted && link.mounted();
        return link;
      }

      // link不存在的话
      const EdgeClass = link.Class || this.theme.edge.Class;
      if (link.type === 'endpoint') {
        let sourceNode = null;
        let targetNode = null;
        let _sourceType = link._sourceType;
        let _targetType = link._targetType;

        if (link.sourceNode instanceof Node || link.sourceNode.__type === 'node') {
          _sourceType = 'node';
          sourceNode = link.sourceNode;
        } else if (link.sourceNode instanceof Group || link.sourceNode.__type === 'group') {
          _sourceType = 'group';
          sourceNode = link.sourceNode;
        } else {
          if (link._sourceType) {
            sourceNode = _sourceType === 'node' ? this.getNode(link.sourceNode) : this.getGroup(link.sourceNode);
          } else {
            let _node = this.getNode(link.sourceNode);
            if (_node) {
              _sourceType = 'node';
              sourceNode = _node;
            } else {
              _sourceType = 'group';
              sourceNode = this.getGroup(link.sourceNode);
            }
          }
        }

        if (link.targetNode instanceof Node || link.targetNode.__type === 'node') {
          _targetType = 'node';
          targetNode = link.targetNode;
        } else if (link.targetNode instanceof Group || link.targetNode.__type === 'group') {
          _targetType = 'group';
          targetNode = link.targetNode;
        } else {
          if (link._targetType) {
            targetNode = _targetType === 'node' ? this.getNode(link.targetNode) : this.getGroup(link.targetNode);
          } else {
            let _node = this.getNode(link.targetNode);
            if (_node) {
              _targetType = 'node';
              targetNode = _node;
            } else {
              _targetType = 'group';
              targetNode = this.getGroup(link.targetNode);
            }
          }
        }

        if (!sourceNode || !targetNode) {
          console.warn(`butterflies error: can not connect edge. link sourceNodeId:${link.sourceNode};link targetNodeId:${link.targetNode}`);
          return;
        }

        let sourceEndpoint = null;
        let targetEndpoint = null;

        if (link.sourceEndpoint && (link.sourceEndpoint instanceof Endpoint || link.sourceEndpoint.__type === 'endpoint')) {
          sourceEndpoint = link.sourceEndpoint;
        } else {
          sourceEndpoint = sourceNode.getEndpoint(link.source, 'source');
        }

        if (link.targetEndpoint && (link.targetEndpoint instanceof Endpoint || link.targetEndpoint.__type === 'endpoint')) {
          targetEndpoint = link.targetEndpoint;
        } else {
          targetEndpoint = targetNode.getEndpoint(link.target, 'target');
        }

        if (!sourceEndpoint || !targetEndpoint) {
          console.warn(`butterflies error: can not connect edge. link sourceId:${link.source};link targetId:${link.target}`);
          return;
        }

        // 线条去重
        if (!this.theme.edge.isRepeat) {
          let _isRepeat = _.some(this.edges, (_edge) => {
            let _result = false;
            if (sourceNode) {
              _result = sourceNode.id === _edge.sourceNode.id && sourceEndpoint.id === _edge.sourceEndpoint.id && _sourceType === _edge.sourceEndpoint.nodeType;
            }

            if (targetNode) {
              _result = _result && (targetNode.id === _edge.targetNode.id && targetEndpoint.id === _edge.targetEndpoint.id && _targetType === _edge.targetEndpoint.nodeType);
            }

            return _result;
          });
          if (_isRepeat) {
            console.warn(`id为${sourceEndpoint.id}-${targetEndpoint.id}的线条连接重复，请检查`);
            return;
          }
        }
        let edge = new EdgeClass({
          type: 'endpoint',
          id: link.id,
          label: link.label,
          shapeType: link.shapeType || this.theme.edge.type,
          orientationLimit: this.theme.endpoint.position,
          isExpandWidth: this.theme.edge.isExpandWidth,
          defaultAnimate: this.theme.edge.defaultAnimate,
          sourceNode,
          targetNode,
          sourceEndpoint,
          targetEndpoint,
          arrow: link.arrow === undefined ? _.get(this, 'theme.edge.arrow') : link.arrow,
          arrowPosition: link.arrowPosition === undefined ? _.get(this, 'theme.edge.arrowPosition') : link.arrowPosition,
          arrowOffset: link.arrowOffset === undefined ? _.get(this, 'theme.edge.arrowOffset') : link.arrowOffset,
          options: link,
          _sourceType,
          _targetType,
          _global: this.global,
          _on: this.on.bind(this),
          _emit: this.emit.bind(this),
        });

        edge._init();

        _edgeFragment.appendChild(edge.dom);

        if (edge.labelDom) {
          _labelFragment.appendChild(edge.labelDom);
        }

        if (edge.arrowDom) {
          _edgeFragment.appendChild(edge.arrowDom);
        }

        this.edges.push(edge);

        sourceEndpoint.connectedNum += 1;
        targetEndpoint.connectedNum += 1;

        edge.mounted && edge.mounted();

        // 假如sourceEndpoint和targetEndpoint没属性，则自动添加上
        if (sourceEndpoint.type === undefined) {
          sourceEndpoint._tmpType = 'source';
        }
        if (targetEndpoint.type === undefined) {
          targetEndpoint._tmpType = 'target';
        }

        return edge;
      } else {
        const sourceNode = this.getNode(link.source);
        const targetNode = this.getNode(link.target);

        if (!sourceNode || !targetNode) {
          console.warn(`butterflies error: can not connect edge. link sourceId:${link.source};link targetId:${link.target}`);
          return;
        }

        let edge = new EdgeClass({
          type: 'node',
          id: link.id,
          label: link.label,
          sourceNode,
          targetNode,
          shapeType: link.shapeType || this.theme.edge.type,
          orientationLimit: this.theme.endpoint.position,
          arrow: link.arrow === undefined ? _.get(this, 'theme.edge.arrow') : link.arrow,
          arrowPosition: link.arrowPosition === undefined ? _.get(this, 'theme.edge.arrowPosition') : link.arrowPosition,
          arrowOffset: link.arrowOffset === undefined ? _.get(this, 'theme.edge.arrowOffset') : link.arrowOffset,
          isExpandWidth: this.theme.edge.isExpandWidth,
          defaultAnimate: this.theme.edge.defaultAnimate,
          _global: this.global,
          _on: this.on.bind(this),
          _emit: this.emit.bind(this),
        });

        edge._init();

        _edgeFragment.appendChild(edge.dom);

        if (edge.labelDom) {
          _labelFragment.appendChild(edge.labelDom);
        }

        if (edge.arrowDom) {
          _edgeFragment.appendChild(edge.arrowDom);
        }

        this.edges.push(edge);

        edge.mounted && edge.mounted();

        return edge;
      }
    }).filter(item => item);

    $(this.svg).append(_edgeFragment);

    $(this.wrapper).append(_labelFragment);

    result.forEach((link) => {
      let _soucePoint = {};
      let _targetPoint = {};
      if (link.type === 'endpoint') {
        _soucePoint = {
          pos: [link.sourceEndpoint._posLeft + link.sourceEndpoint._width / 2, link.sourceEndpoint._posTop + link.sourceEndpoint._height / 2],
          orientation: link.sourceEndpoint.orientation ? link.sourceEndpoint.orientation : undefined
        };
        _targetPoint = {
          pos: [link.targetEndpoint._posLeft + link.targetEndpoint._width / 2, link.targetEndpoint._posTop + link.targetEndpoint._height / 2],
          orientation: link.targetEndpoint.orientation ? link.targetEndpoint.orientation : undefined
        };
      } else if (link.type === 'node') {
        _soucePoint = {
          pos: [link.sourceNode.left + link.sourceNode.getWidth() / 2, link.sourceNode.top + link.sourceNode.getHeight() / 2]
        };

        _targetPoint = {
          pos: [link.targetNode.left + link.targetNode.getWidth() / 2, link.targetNode.top + link.targetNode.getHeight() / 2]
        };
      }
      link.redraw(_soucePoint, _targetPoint);
    });

    if (!isNotEventEmit) {
      this.pushActionQueue({
        type: 'system:addEdges',
        data: result
      });
      this.emit('system.link.connect', {
        links: result
      });
      this.emit('events', {
        type: 'link:connect',
        links: result
      });
    }

    $(this.svg).css('visibility', 'visible');

    return result;
  }
  addEdge(link, isNotEventEmit) {
    return this.addEdges([link], isNotEventEmit)[0];
  }
  removeEdges(edges, isNotEventEmit, isNotPushActionQueue) {
    let result = [];
    edges.forEach((_edge) => {
      let edgeIndex = -1;
      if (_edge instanceof Edge || _edge.__type === 'edge') {
        if (_edge.sourceEndpoint) {
          _edge.sourceEndpoint.connectedNum -= 1;
        }
        if (_edge.targetEndpoint) {
          _edge.targetEndpoint.connectedNum -= 1;
        }
        edgeIndex = _.findIndex(this.edges, (item) => {
          if (item.type === 'node') {
            return _edge.sourceNode.id === item.sourceNode.id && _edge.targetNode.id === item.targetNode.id;
          } else {
            return (
              _.get(_edge, 'sourceNode.id') === _.get(item, 'sourceNode.id') &&
              _.get(_edge, 'sourceEndpoint.id') === _.get(item, 'sourceEndpoint.id') &&
              _.get(_edge, 'sourceEndpoint.nodeType') === _.get(item, 'sourceEndpoint.nodeType')
            ) && (
                _.get(_edge, 'targetNode.id') === _.get(item, 'targetNode.id') &&
                _.get(_edge, 'targetEndpoint.id') === _.get(item, 'targetEndpoint.id') &&
                _.get(_edge, 'targetEndpoint.nodeType') === _.get(item, 'targetEndpoint.nodeType')
              );
          }
        });
      } else if (_.isString(_edge)) {
        edgeIndex = _.findIndex(this.edges, (item) => {
          return _edge === item.id;
        });
      } else {
        console.warn(`删除线条错误，传入参数有误，请检查`);
        return;
      }
      if (edgeIndex !== -1) {
        result = result.concat(this.edges.splice(edgeIndex, 1));
      } else {
        console.warn(`删除线条错误，不存在值为${_edge.id}的线`);
      }
    });

    if (!isNotPushActionQueue) {
      this.pushActionQueue({
        type: 'system:removeEdges',
        data: result
      });
    }

    if (!isNotEventEmit) {
      this.emit('system.links.delete', {
        links: result
      });
      this.emit('events', {
        type: 'links:delete',
        links: result
      });
    }


    result.forEach((item) => {
      item.destroy(isNotEventEmit);
    });

    // 把endpoint重新赋值
    result.forEach((_rmEdge) => {
      if (_.get(_rmEdge, 'sourceEndpoint._tmpType') === 'source') {
        let isExistEdge = _.some(this.edges, (edge) => {
          return _rmEdge.sourceNode.id === edge.sourceNode.id && _rmEdge.sourceEndpoint.id === edge.sourceEndpoint.id;
        });
        !isExistEdge && (_rmEdge.sourceEndpoint._tmpType = undefined);
      }
      if (_.get(_rmEdge, 'targetEndpoint._tmpType') === 'target') {
        let isExistEdge = _.some(this.edges, (edge) => {
          return _rmEdge.targetNode.id === edge.targetNode.id && _rmEdge.targetEndpoint.id === edge.targetEndpoint.id;
        });
        !isExistEdge && (_rmEdge.targetEndpoint._tmpType = undefined);
      }
    });
    return result;
  }
  removeEdge(edge, isNotEventEmit, isNotPushActionQueue) {
    return this.removeEdges([edge], isNotEventEmit, isNotPushActionQueue)[0];
  }
  getNeighborEdges(id, type) {

    let node = undefined;
    let group = undefined;
    if (type === 'node') {
      node = _.find(this.nodes, item => id === item.id);
    } else if (type === 'group') {
      group = _.find(this.groups, item => id === item.id);
    } else {
      node = _.find(this.nodes, item => id === item.id);
      node && !type && (type = 'node');
      group = _.find(this.groups, item => id === item.id);
      group && !type && (type = 'group');
    }

    return this.edges.filter((item) => {
      if (type === 'node') {
        return _.get(item, 'sourceNode.id') === node.id || _.get(item, 'targetNode.id') === node.id;
      } else {
        return _.get(item, 'sourceNode.id') === group.id || _.get(item, 'targetNode.id') === group.id;
      }
    });
  }
  getNeighborEdgesByEndpoint(id, pointId) {
    let edges = this.getNeighborEdges(id);
    return edges.filter((item) => {
      if (item.type === 'node') {
        return false;
      } else {
        return (
          (item.sourceNode.id === id && item.sourceEndpoint.id === pointId) || 
          (item.targetNode.id === id && item.targetEndpoint.id === pointId)
        );
      }
    });
  }
  // 设置线段的z-index
  setEdgeZIndex(edges = [], zIndex = 0) {
    if (edges.length === 0) {
      return;
    }
    edges.forEach((edge) => {
      edge._zIndex = zIndex;
      let index = _.findIndex(this.edges, (item) => {
        return item === edge;
      });
      if (index !== -1) {
        let delEdge = this.edges.splice(index, 1);
        $(delEdge.dom).detach();
        delEdge.eventHandlerDom && $(delEdge.eventHandlerDom).detach();
        delEdge.arrowDom && $(delEdge.arrowDom).detach();
      } else {
        return;
      }
    });
    let addIndex = this._findEdgeIndex(edges[0]);
    let addEdgesDom = [];
    edges.forEach((item) => {
      addEdgesDom.push(item.dom);
      item.eventHandlerDom && addEdgesDom.push(item.eventHandlerDom);
      item.arrowDom && addEdgesDom.push(item.arrowDom);
    });

    // 插入dom
    let beforeEdge = this.edges[addIndex];
    let afterEdge = this.edges[addIndex + 1];
    
    if (beforeEdge) {
      let targetDom = beforeEdge.dom;
      beforeEdge.eventHandlerDom && (targetDom = beforeEdge.eventHandlerDom);
      beforeEdge.arrowDom && (targetDom = beforeEdge.arrowDom);
      $(targetDom).after(addEdgesDom);
    } else if (afterEdge) {
      $(afterEdge.dom).before(addEdgesDom);
    } else {
      $(this.svg).append(addEdgesDom);
    }

    this.edges.splice(addIndex + 1, 0 , ...edges);
  }
  _findEdgeIndex(edge) {
    let index = 0;
    let currentZIndex = edge._zIndex || 0;
    this.edges.forEach((item, i) => {
      if (currentZIndex < (item._zIndex || 0)) {
        index = i;
        return;
      }
      if (i === this.edges.length - 1) {
        index = i;
      }
    });
    return index;
  }


  //===============================
  //[ 布局配置 ]
  //===============================
  _autoLayout(data) {
    const width = this._rootWidth;
    const height = this._rootHeight;

    if (_.isFunction(this.layout)) {
      this.layout({
        width: width,
        height: height,
        data: data
      });
    } else {
      // 重力布局
      if (_.get(this.layout, 'type') === 'forceLayout') {
        const _opts = $.extend({
          // 布局画布总宽度
          width,
          // 布局画布总长度
          height,
          // 布局相对中心点
          center: {
            x: width / 2,
            y: height / 2
          },
          // 节点互斥力，像电荷原理一样
          chargeStrength: -150,
          link: {
            // 以node的什么字段为寻找id，跟d3原理一样
            id: 'id',
            // 线条的距离
            distance: 200,
            // 线条的粗细
            strength: 1
          }
        }, _.get(this.layout, 'options'), true);

        // 自动布局
        if (_.get(this.layout, 'type') === 'forceLayout') {
          Layout.forceLayout({
            opts: _opts,
            data: {
              groups: data.groups,
              nodes: data.nodes,
              // 加工线条数据，兼容endpoint为id的属性，d3没这个概念
              edges: data.edges.map(item => ({
                source: item.type === 'endpoint' ? item.sourceNode : item.source,
                target: item.type === 'endpoint' ? item.targetNode : item.target
              }))
            }
          });
        }
      } else if (_.get(this.layout, 'type') === 'dagreLayout') {
        Layout.dagreLayout({
          //  /** layout 方向, 可选 TB, BT, LR, RL */
          // public rankdir: 'TB' | 'BT' | 'LR' | 'RL' = 'TB';
          rankdir: _.get(this.layout, 'options.rankdir') || 'TB',
          // /** 节点对齐方式，可选 UL, UR, DL, DR */
          // public align: undefined | 'UL' | 'UR' | 'DL' | 'DR';
          align: _.get(this.layout, 'options.align'),
          // /** 节点大小 */
          // public nodeSize: number | number[] | undefined;
          nodeSize: _.get(this.layout, 'options.nodeSize'),
          // /** 节点水平间距(px) */
          // public nodesepFunc: ((d?: any) => number) | undefined;
          nodesepFunc: _.get(this.layout, 'options.nodesepFunc'),
          // /** 每一层节点之间间距 */
          // public ranksepFunc: ((d?: any) => number) | undefined;
          ranksepFunc: _.get(this.layout, 'options.ranksepFunc'),
          // /** 节点水平间距(px) */
          // public nodesep: number = 50;
          nodesep: _.get(this.layout, 'options.nodesep') || 50,
          // /** 每一层节点之间间距 */
          // public ranksep: number = 50;
          ranksep: _.get(this.layout, 'options.ranksep') || 50,
          // /** 是否保留布局连线的控制点 */
          // public controlPoints: boolean = false;
          controlPoints: _.get(this.layout, 'options.controlPoints') || false,
          data: {
            // groups: data.groups,
            nodes: data.nodes,
            edges: data.edges.map(item => ({
              source: item.type === 'endpoint' ? item.sourceNode : item.source,
              target: item.type === 'endpoint' ? item.targetNode : item.target
            }))
          }
        })
      } else if (_.get(this.layout, 'type') === 'concentricLayout') {
        Layout.concentLayout({
          /** 布局中心 默认值：图的中心 */
          // public center: [number, number];
          center: _.get(this.layout, 'options.center') || [width / 2, height / 2],
          // /** 节点大小（直径）。用于防止节点重叠时的碰撞检测 */
          // public nodeSize: number | [number, number] = 30;
          nodeSize: _.get(this.layout, 'options.nodeSize') || 30,
          // /** 环与环之间最小间距，用于调整半径 */
          // public minNodeSpacing: number = 10;
          minNodeSpacing: _.get(this.layout, 'options.minNodeSpacing') || 10,
          // /** 是否防止重叠，必须配合上面属性 nodeSize，只有设置了与当前图节点大小相同的 nodeSize 值，才能够进行节点重叠的碰撞检测 */
          // public preventOverlap: boolean = false;
          preventOverlap: _.get(this.layout, 'options.preventOverlap') || false,
          // /** 第一个节点与最后一个节点之间的弧度差。若为 undefined ，则将会被设置为  2 _ Math.PI _ (1 - 1 / |level.nodes|) ，其中 level.nodes 为该算法计算出的每一层的节点，|level.nodes| 代表该层节点数量 */
          // public sweep: number | undefined;
          sweep: _.get(this.layout, 'options.sweep'),
          // /** 环与环之间的距离是否相等 */
          // public equidistant: boolean = false;
          equidistant: _.get(this.layout, 'options.equidistant') || false,
          // /** 开始方式节点的弧度 */
          // public startAngle: number = (3 / 2) * Math.PI;
          startAngle: (3 / 2) * Math.PI,
          // /** 是否按照顺时针排列 */
          // public clockwise: boolean = true;
          clockwise: _.get(this.layout, 'options.clockwise') || true,
          // /** 每一层同心值的求和。若为 undefined，则将会被设置为 maxValue / 4 ，其中 maxValue 为最大的排序依据的属性值。例如，若 sortBy 为 'degree'，则 maxValue 为所有节点中度数最大的节点的度数 */
          //public maxLevelDiff: undefined | number;
          maxLevelDiff: _.get(this.layout, 'options.maxLevelDiff'),
          // /** 指定排序的依据（节点属性名），数值越高则该节点被放置得越中心。若为 undefined，则会计算节点的度数，度数越高，节点将被放置得越中心 */
          // public sortBy: string = 'degree';
          sortBy: _.get(this.layout, 'options.sortBy') || 'degree',
          // 布局画布总宽度
          width: width,
          // 布局画布总长度
          height: height,
          data: {
            groups: data.groups,
            nodes: data.nodes,
            edges: data.edges.map(item => ({
              source: item.type === 'endpoint' ? item.sourceNode : item.source,
              target: item.type === 'endpoint' ? item.targetNode : item.target
            }))
          }
        });
      } else if (_.get(this.layout, 'type') === 'circleLayout') {
        Layout.circleLayout({
          radius: _.get(this.layout, 'options.radius'),
          getWidth: _.get(this.layout, 'options.getWidth'),
          getHeight: _.get(this.layout, 'options.getHeight'),
          data: {
            nodes: data.nodes,
            edges: data.edges.map(item => ({
              source: item.type === 'endpoint' ? item.sourceNode : item.source,
              target: item.type === 'endpoint' ? item.targetNode : item.target
            }))
          }
        });
      } else if(_.get(this.layout, 'type') === 'gridLayout') {
        const _opts = $.extend({
          // 布局画布总宽度
          width:  _.get(this.layout, 'width') || 150,
          // 布局画布总长度
          height: _.get(this.layout, 'height') || 100,
          // 布局相对起始点
          begin: _.get(this.layout, 'begin') || [0, 0],
          center:  _.get(this.layout, 'center') || [width / 2, height / 2],
          preventOverlap: _.get(this.layout, 'preventOverlap') || true,
          preventOverlapPadding: _.get(this.layout, 'preventOverlapPadding') || 10,
          condense: _.get(this.layout, 'condense') || false,
          //宽高
          rows: _.get(this.layout, 'rows'),
          cols: _.get(this.layout, 'cols'),
          //位置
          position: _.get(this.layout, 'position'),
          // 排序方式
          sortBy: _.get(this.layout, 'sortBy') || 'degree',
          nodeSize: _.get(this.layout, 'nodeSize') || 30,
          link: {
            // 以node的什么字段为寻找id，跟d3原理一样
            id: 'id',
            // 线条的距离
            distance: 100,
            // 线条的粗细
            strength: 1
          }
        }, _.get(this.layout, 'options'), true);
        // 自动布局
        if (_.get(this.layout, 'type') === 'gridLayout') {
          Layout.gridLayout({
            opts: _opts,
            data: {
              groups: data.groups,
              nodes: data.nodes,
              // 加工线条数据，兼容endpoint为id的属性，d3没这个概念
              edges: data.edges.map(item => ({
                source: item.type === 'endpoint' ? item.sourceNode : item.source,
                target: item.type === 'endpoint' ? item.targetNode : item.target
              }))
            }
          })
        }
      } else if(_.get(this.layout, 'type') === 'fruchterman') {
        const _opts = $.extend({
           // 布局画布总宽度
           width,
           // 布局画布总长度
           height,
            /** 停止迭代的最大迭代数 */
          maxIteration: 1000,
            /** 布局中心 */
          center: [width / 2, height / 2],
            /** 重力大小，影响图的紧凑程度 */
          gravity: 5,
          /** 速度 */
          speed: 5,
           /** 是否产生聚类力 */
          clustering: false,
           /** 聚类力大小 */
          clusterGravity: 10,
          link: {
            // 以node的什么字段为寻找id，跟d3原理一样
            id: 'id',
            // 线条的距离
            distance: 100,
            // 线条的粗细
            strength: 1
          }
        }, _.get(this.layout, 'options'), true);
        // 自动布局
        if (_.get(this.layout, 'type') === 'fruchterman') {

          Layout.fruchterman({
            opts: _opts,
            data: {
              groups: data.groups,
              nodes: data.nodes,
              // 加工线条数据，兼容endpoint为id的属性，d3没这个概念
              edges: data.edges.map(item => ({
                source: item.type === 'endpoint' ? item.sourceNode : item.source,
                target: item.type === 'endpoint' ? item.targetNode : item.target
              }))
            }
          })
        }
      }else if(_.get(this.layout, 'type') === 'radial') {
        const _opts = $.extend({
          // 布局画布总宽度
          width: _.get(this.layout, 'options.width') || 500,
          // 布局画布总长度
          height: _.get(this.layout, 'options.height') || 500,
           /** 停止迭代的最大迭代数 */
         maxIteration:  _.get(this.layout, 'options.maxIteration')|| 1000,
           /** 布局中心 */
         center: _.get(this.layout, 'options.center')|| [width / 2, height / 2],
           /** 中心点，默认为数据中第一个点 */
         focusNode: _.get(this.layout, 'options.focusNode') || null,
           /** 每一圈半径 */
         unitRadius: _.get(this.layout, 'options.unitRadius') || null,
           /** 默认边长度 */
         linkDistance: _.get(this.layout, 'options.linkDistance') || 50,
           /** 是否防止重叠 */
         preventOverlap: _.get(this.layout, 'options.preventOverlap') || false,
           /** 节点直径 */
         nodeSize: _.get(this.layout, 'options.nodeSize') || undefined,
           /** 节点间距，防止节点重叠时节点之间的最小距离（两节点边缘最短距离） */
         nodeSpacing: _.get(this.layout, 'options.nodeSpacing') || undefined,
           /** 是否必须是严格的 radial 布局，即每一层的节点严格布局在一个环上。preventOverlap 为 true 时生效 */
         strictRadial: _.get(this.layout, 'options.strictRadial') || true,
           /** 防止重叠步骤的最大迭代次数 */
         maxPreventOverlapIteration: _.get(this.layout, 'options.maxPreventOverlapIteration') || 200,
         sortBy: _.get(this.layout, 'options.sortBy') || undefined,
         sortStrength: _.get(this.layout, 'options.sortStrength') || 10,
         link: {
           // 以node的什么字段为寻找id，跟d3原理一样
           id: 'id',
           // 线条的距离
           distance: 100,
           // 线条的粗细
           strength: 1
         }
       }, _.get(this.layout, 'options'), true);
       // 自动布局
       if (_.get(this.layout, 'type') === 'radial') {
         Layout.radial({
           opts: _opts,
           data: {
             groups: data.groups,
             nodes: data.nodes,
             // 加工线条数据，兼容endpoint为id的属性，d3没这个概念
             edges: data.edges.map(item => ({
               source: item.type === 'endpoint' ? item.sourceNode : item.source,
               target: item.type === 'endpoint' ? item.targetNode : item.target
             }))
           }
         })
       }
      }
    }
  }


  // minmap
  setMinimap(flat = true, options = {}) {
    if (!options.events) {
      options.events = [];
    }

    const updateEvts = [
      'system.canvas.zoom',
      'system.node.delete',
      'system.node.move',
      'system.nodes.add',
      'system.group.delete',
      'system.group.move',
      'system.drag.move',
      'system.canvas.move',
      ...options.events
    ];

    const getNodes = () => {
      return this.nodes.map(node => {
        return {
          id: node.id,
          left: node.left,
          top: node.top,
          width: node.getWidth(),
          height: node.getHeight(),
          group: node.group,
          minimapActive: _.get(node, 'options.minimapActive')
        }
      });
    }

    const getGroups = () => {
      return this.groups.map(group => {
        return {
          id: group.id,
          left: group.left,
          top: group.top,
          width: group.getWidth(),
          height: group.getHeight(),
          minimapActive: _.get(group, 'options.minimapActive')
        }
      });
    }

    if (flat && !this.minimap) {
      this.minimap = new Minimap({
        root: this.root,
        move: this.move.bind(this),
        terminal2canvas: this.terminal2canvas.bind(this),
        canvas2terminal: this.canvas2terminal.bind(this),
        nodes: getNodes(),
        groups: getGroups(),
        zoom: this.getZoom(),
        offset: this.getOffset(),
        ...options
      });
      this.updateFn = () => {
        this.minimap.update({
          nodes: getNodes(),
          groups: getGroups(),
          zoom: this.getZoom(),
          offset: this.getOffset()
        });
      };
      for (let ev of updateEvts) {
        this.on(ev, this.updateFn);
      }
      return;
    }

    if (!this.minimap) {
      return;
    }

    this.minimap.destroy();
    for (let ev of updateEvts) {
      this.off(ev, this.updateFn);
    }

    delete this.minimap;
    delete this.updateFn;
  }


  //===============================
  //[ 聚焦处理 ]
  //===============================
  focusNodesWithAnimate(param, type = ['node'], options, callback) {
    // 画布里的可视区域
    let canLeft = Infinity;
    let canRight = -Infinity;
    let canTop = Infinity;
    let canBottom = -Infinity;
    if (_.includes(type, 'node')) {
      let nodeIds = param.nodes;
      this.nodes.filter((_node) => {
        return _.find(nodeIds, (id) => {
          return _node.id === id;
        }) !== undefined;
      }).forEach((_node) => {
        let _nodeLeft = _node.left;
        let _nodeRight = _node.left + _node.getWidth();
        let _nodeTop = _node.top;
        let _nodeBottom = _node.top + _node.getHeight();
        if (_node.group) {
          let group = this.getGroup(_node.group);
          if (group) {
            _nodeLeft += group.left;
            _nodeRight += group.left;
            _nodeTop += group.top;
            _nodeBottom += group.top;
          }
        }
        if (_nodeLeft < canLeft) {
          canLeft = _nodeLeft;
        }
        if (_nodeRight > canRight) {
          canRight = _nodeRight;
        }
        if (_nodeTop < canTop) {
          canTop = _nodeTop;
        }
        if (_nodeBottom > canBottom) {
          canBottom = _nodeBottom;
        }
      });
    }

    if (_.includes(type, 'group')) {
      let groupIds = param.groups;
      this.groups.filter((_group) => {
        return _.find(groupIds, (id) => {
          return id === _group.id;
        });
      }).forEach((_group) => {
        let _groupLeft = _group.left;
        let _groupRight = _group.left + _group.getWidth();
        let _groupTop = _group.top;
        let _groupBottom = _group.top + _group.getHeight();
        if (_groupLeft < canLeft) {
          canLeft = _groupLeft;
        }
        if (_groupRight > canRight) {
          canRight = _groupRight;
        }
        if (_groupTop < canTop) {
          canTop = _groupTop;
        }
        if (_groupBottom > canBottom) {
          canBottom = _groupBottom;
        }
      });
    }
    let customOffset = _.get(options, 'offset') || [0, 0];
    let canDisX = canRight - canLeft;
    let terDisX = this._rootWidth - customOffset[0];
    let canDisY = canBottom - canTop;
    let terDisY = this._rootHeight - customOffset[1];
    let scaleX = terDisX / canDisX;
    let scaleY = terDisY / canDisY;
    // 这里要根据scale来判断
    let scale = scaleX < scaleY ? scaleX : scaleY;
    if (_.get(options, 'keepPreZoom')) {
      scale = this._zoomData < scale ? this._zoomData : scale;
    } else {
      scale = 1 < scale ? 1 : scale;
    }
    let terLeft = this._coordinateService._canvas2terminal('x', canLeft, {
      scale: scale,
      canOffsetX: 0,
      canOffsetY: 0,
      terOffsetX: 0,
      terOffsetY: 0,
      originX: 50,
      originY: 50
    });
    let terRight = this._coordinateService._canvas2terminal('x', canRight, {
      scale: scale,
      canOffsetX: 0,
      canOffsetY: 0,
      terOffsetX: 0,
      terOffsetY: 0,
      originX: 50,
      originY: 50
    });
    let terTop = this._coordinateService._canvas2terminal('y', canTop, {
      scale: scale,
      canOffsetX: 0,
      canOffsetY: 0,
      terOffsetX: 0,
      terOffsetY: 0,
      originX: 50,
      originY: 50
    });
    let terBottom = this._coordinateService._canvas2terminal('y', canBottom, {
      scale: scale,
      canOffsetX: 0,
      canOffsetY: 0,
      terOffsetX: 0,
      terOffsetY: 0,
      originX: 50,
      originY: 50
    });

    let offsetX = (terLeft + terRight - this._rootWidth) / 2;
    let offsetY = (terTop + terBottom - this._rootHeight) / 2;

    offsetX = -offsetX + customOffset[0];
    offsetY = -offsetY + customOffset[1];

    const time = 500;
    $(this.wrapper).animate({
      top: offsetY,
      left: offsetX,
    }, time);
    this._moveData = [offsetX, offsetY];

    this._coordinateService._changeCanvasInfo({
      canOffsetX: offsetX,
      canOffsetY: offsetY,
      scale: scale,
      originX: 50,
      originY: 50
    });

    this.zoom(scale, callback);
  }
  focusCenterWithAnimate(options, callback) {
    let nodeIds = this.nodes.map((item) => {
      return item.id;
    });
    let groupIds = this.groups.map((item) => {
      return item.id;
    });

    this.focusNodesWithAnimate({
      nodes: nodeIds,
      groups: groupIds
    }, ['node', 'group'], options, callback);
  }
  focusNodeWithAnimate(param, type = 'node', options, callback) {
    let node = null;

    if (_.isFunction(param)) { // 假如传入的是filter，则按照用户自定义的规则来寻找
      node = type === 'node' ? _.find(this.nodes, param) : _.find(this.groups, param);
    } else { // 假如传入的是id，则按照默认规则寻找
      node = type === 'node' ? _.find(this.nodes, item => item.id === param) : _.find(this.groups, item => item.id === param);
    }

    let top = 0;
    let left = 0;
    if (!node) {
      return;
    }
    top = node.top || node.y;
    left = node.left || node.x;
    if (node.height) {
      top += node.height / 2;
    }
    if (node.width) {
      left += node.width / 2;
    }

    if (node.group) {
      const group = _.find(this.groups, _group => _group.id === node.group);
      if (!group) return;
      top += group.top || group.y;
      left += group.left || group.x;
      if (group.height) {
        top += group.height / 2;
      }
      if (group.width) {
        left += group.width / 2;
      }
    }

    let customOffset = _.get(options, 'offset') || [0, 0];

    const containerW = this._rootWidth;
    const containerH = this._rootHeight;

    const targetY = containerH / 2 - top + customOffset[1];
    const targetX = containerW / 2 - left + customOffset[0];

    const time = 500;

    // animate不支持scale，使用setInterval自己实现
    $(this.wrapper).animate({
      top: targetY,
      left: targetX,
    }, time);
    this._moveData = [targetX, targetY];

    this._coordinateService._changeCanvasInfo({
      canOffsetX: targetX,
      canOffsetY: targetY,
      originX: 50,
      originY: 50,
      scale: 1
    });

    this.zoom(1, callback);

    this._guidelineService.isActive && this._guidelineService.clearCanvas();
  }

  //===============================
  //[ 框选处理 ]
  //===============================
  setSelectMode(flat = true, contents = ['node'], selecMode = 'include') {
    if (flat) {
      this.isSelectMode = true;
      this._rmSystemUnion();
      this.selecContents = contents;
      this.selecMode = selecMode;
      this.canvasWrapper.active();
      this._remarkMove = this.moveable;
      this._remarkZoom = this.zoomable;
      this.setZoomable(false);
      this.setMoveable(false);
    } else {
      this.isSelectMode = false;
      this.canvasWrapper.unActive();
      if (this._remarkMove) {
        this.setMoveable(true);
      }
      if (this._remarkZoom) {
        this.setZoomable(true);
      }
    }
  }
  getUnion(name) {
    if (!name) {
      console.error('传入正确的name');
      return;
    }
    return this._unionData[name];
  }

  getAllUnion() {
    return this._unionData;
  }

  add2Union(name, obj) {
    if (!name || !obj) {
      return;
    }

    if (!this._unionData[name]) {
      this._unionData[name] = {
        nodes: [],
        groups: [],
        edges: [],
        endpoints: []
      }
    }

    let _data = this._unionData[name];
    if (obj.nodes) {
      obj.nodes.forEach((item) => {
        let isId = _.isString(item);
        let node = isId ? this.getNode(item) : item;
        _data.nodes.push(node);
      });
      _data.nodes = _.uniqBy(_data.nodes, 'id');
    }

    if (obj.groups) {
      obj.groups.forEach((item) => {
        let isId = _.isString(item);
        let group = isId ? this.getGroup(item) : item;
        _data.groups.push(group);
      });
      _data.groups = _.uniqBy(_data.groups, 'id');
    }

    if (obj.edges) {
      obj.edges.forEach((item) => {
        let isId = _.isString(item);
        let edge = isId ? this.getEdge(item) : item;
        _data.edges.push(edge);
      });
      _data.edges = _.uniqBy(_data.edges, 'id');
    }

    if (obj.endpoints) {
      _data.endpoints = _data.endpoints.concat(obj.endpoints);
    }
  }
  removeUnion(name) {
    this._unionData[name] = {
      nodes: [],
      edges: [],
      groups: [],
      endpoints: []
    };
  }
  removeAllUnion() {
    this._unionData = {
      __system: {
        nodes: [],
        edges: [],
        groups: [],
        endpoints: []
      }
    };
  }
  _rmSystemUnion() {
    this._unionData['__system'].nodes = [];
    this._unionData['__system'].edges = [];
    this._unionData['__system'].groups = [];
    this._unionData['__system'].endpoints = [];
  }

  _findUnion(type, item) {
    let result = [];
    for (let key in this._unionData) {
      let isExist = _.find(_.get(this._unionData, [key, type], []), (_item) => {
        return _.toString(_item.id) === _.toString(item.id);
      });
      if (isExist) {
        result.push(key);
      }
    }
    return result;
  }
  _selectMultiplyItem(range, toDirection) {

    // 确认一下终端的偏移值
    const startX = this._coordinateService._terminal2canvas('x', range[0]);
    const startY = this._coordinateService._terminal2canvas('y', range[1]);
    const endX = this._coordinateService._terminal2canvas('x', range[2]);
    const endY = this._coordinateService._terminal2canvas('y', range[3]);

    const includeNode = _.includes(this.selecContents, 'node');
    const includeEdge = _.includes(this.selecContents, 'edge');
    const includeEndpoint = _.includes(this.selecContents, 'endpoint');

    let _isSelected = (option) => {
      let _itemLeft = option.left;
      let _itemRight = option.right;
      let _itemTop = option.top;
      let _itemBottom = option.bottom;
      if (this.selecMode === 'include' || (this.selecMode === 'senior' && toDirection === 'right')) {
        return startX < _itemLeft && endX > _itemRight && startY < _itemTop && endY > _itemBottom;
      }
      if (this.selecMode === 'touch' || (this.selecMode === 'senior' && toDirection === 'left')) {
        let result = true;
        if (endX < _itemLeft) {
          result = false;
        }
        if (startX > _itemRight) {
          result = false;
        }
        if (endY < _itemTop) {
          result = false;
        }
        if (startY > _itemBottom) {
          result = false;
        }
        return result;
      }
    }

    // 框选节点
    if (includeNode) {
      this.nodes.forEach((item) => {
        let nodeLeft = item.left;
        let nodeRight = item.left + $(item.dom).width();
        let nodeTop = item.top;
        let nodeBottom = item.top + $(item.dom).height();
        if (item.group) {
          let _group = this.getGroup(item.group);
          nodeLeft += _group.left;
          nodeRight += _group.left;
          nodeTop += _group.top;
          nodeBottom += _group.top;
        }
        let isSelected = _isSelected({
          left: nodeLeft,
          right: nodeRight,
          top: nodeTop,
          bottom: nodeBottom
        });
        if (isSelected) {
          this.selectItem.nodes.push(item);
        }
      });
    }

    // 框选锚点
    if (includeEndpoint) {
      this.nodes.forEach((node) => {
        node.endpoints.forEach((item) => {
          const pointLeft = item._posLeft;
          const pointRight = item._posLeft + $(item.dom).width();
          const pointTop = item._posTop;
          const pointBottom = item._posTop + $(item.dom).height();
          let isSelected = _isSelected({
            left: pointLeft,
            right: pointRight,
            top: pointTop,
            bottom: pointBottom
          });
          if (isSelected) {
            this.selectItem.endpoints.push(item);
          }
        });
      });
    }

    // 框选线条
    if (includeEdge) {
      this.edges.forEach((item) => {
        if (item.type === 'endpoint') {
          const left = item.sourceEndpoint._posLeft < item.targetEndpoint._posLeft ? item.sourceEndpoint._posLeft : item.targetEndpoint._posLeft;
          const right = (item.sourceEndpoint._posLeft + item.sourceEndpoint._width) > (item.targetEndpoint._posLeft + item.targetEndpoint._width) ? (item.sourceEndpoint._posLeft + item.sourceEndpoint._width) : (item.targetEndpoint._posLeft + item.targetEndpoint._width);
          const top = item.sourceEndpoint._posTop < item.targetEndpoint._posTop ? item.sourceEndpoint._posTop : item.targetEndpoint._posTop;
          const bottom = (item.sourceEndpoint._posTop + item.sourceEndpoint._height) > (item.targetEndpoint._posTop + item.targetEndpoint._height) ? (item.sourceEndpoint._posTop + item.sourceEndpoint._height) : (item.targetEndpoint._posTop + item.targetEndpoint._height);
          let isSelected = _isSelected({
            left: left,
            right: right,
            top: top,
            bottom: bottom
          });
          if (isSelected) {
            this.selectItem.edges.push(item);
          }
        } else if (item.type === 'node') {
          // 后续补
        }
      });
    }

    // 框选节点组，准备支持

    return this.selectItem;
  }


  //===============================
  //[ 辅助方法 ]
  //===============================
  updateRootResize(opts = {}) {
    this._coordinateService._changeCanvasInfo({
      terOffsetX: opts.terOffsetX || $(this.root).offset().left,
      terOffsetY: opts.terOffsetY || $(this.root).offset().top,
      terWidth: opts.terWidth || $(this.root).width(),
      terHeight: opts.terHeight || $(this.root).height()
    });
    this.canvasWrapper._changeCanvasInfo({
      terScrollX: opts.terScrollX || 0,
      terScrollY: opts.terScrollY || 0
    });
    this.canvasWrapper.resize({
      root: this.root
    });
    this._gridService._resize();
    this._guidelineService._resize();
  }
  setZoomable(flat, zoomDirection = this._zoomDirection) {
    if (zoomDirection !== undefined) {
      this._zoomDirection = zoomDirection;
    }
    if (!this._zoomCb) {
      this._zoomCb = (event) => {
        event.preventDefault();
        const deltaY = event.deltaY;
        if (this._zoomDirection) {
          this._zoomData -= deltaY * this.theme.zoomGap;
        } else {
          this._zoomData += deltaY * this.theme.zoomGap;
        }

        if (this._zoomData < 0.25) {
          this._zoomData = 0.25;
          return;
        } if (this._zoomData > 5) {
          this._zoomData = 5;
          return;
        }

        const platform = ['webkit', 'moz', 'ms', 'o'];
        const scale = `scale(${this._zoomData})`;
        for (let i = 0; i < platform.length; i++) {
          this.wrapper.style[`${platform[i]}Transform`] = scale;
        }
        this.wrapper.style.transform = scale;
        this._coordinateService._changeCanvasInfo({
          wrapper: this.wrapper,
          girdWrapper: this._guidelineService.dom,
          mouseX: event.clientX,
          mouseY: event.clientY,
          scale: this._zoomData
        });
        this._guidelineService.zoom(this._zoomData);
        this.emit('system.canvas.zoom', {
          zoom: this._zoomData
        });
        this.emit('events', {
          type: 'canvas.zoom',
          zoom: this._zoomData
        });
      };
    }

    if (flat) {
      // 双指Mac下缩放正常，Window鼠标滑轮方向相反
      this.root.addEventListener('wheel', this._zoomCb);
    } else {
      this.root.removeEventListener('wheel', this._zoomCb);
    }
  }
  setMoveable(flat) {
    if (flat) {
      this.moveable = true;
      if (this._dragType === 'canvas:drag') {
        this.moveable = false;
      }
    } else {
      this.moveable = false;
    }
  }
  setLinkable(flat) {
    this.linkable = !!flat;
  }
  setDisLinkable(flat) {
    this.disLinkable = !!flat;
  }
  setDraggable(flat) {
    this.nodes.forEach((node) => {
      node.setDraggable(flat);
    });
    this.draggable = flat;
  }
  setOrigin(data) {
    let originX = (data[0] || '0').toString().replace('%', '');
    let originY = (data[1] || '0').toString().replace('%', '');
    this._coordinateService._changeCanvasInfo({
      originX: parseFloat(originX),
      originY: parseFloat(originY)
    });
  }
  getZoom() {
    return this._zoomData;
  }
  getOffset() {
    return this._moveData;
  }
  getOrigin() {
    return [this._coordinateService.originX + '%', this._coordinateService.originY + '%']
  }
  zoom(param, callback) {
    if (param < 0.25) {
      param = 0.25;
    } if (param > 5) {
      param = 5;
    }
    const time = 50;
    let frame = 1;
    const gap = param - this._zoomData;
    const interval = gap / 20;
    clearInterval(this._zoomTimer);
    this._zoomTimer = null;
    if (gap !== 0) {
      this._zoomTimer = setInterval(() => {
        this._zoomData += interval;
        let _canvasInfo = {
          scale: this._zoomData
        };
        if (this._coordinateService.originX === undefined || this._coordinateService.originY === undefined) {
          _canvasInfo['originX'] = 50;
          _canvasInfo['originY'] = 50;
        }
        this._coordinateService._changeCanvasInfo(_canvasInfo);
        this._guidelineService.zoom(this._zoomData);
        $(this.wrapper).css({
          transform: `scale(${this._zoomData})`
        });
        if (frame === 20) {
          clearInterval(this._zoomTimer);
          this.emit('system.canvas.zoom', {
            zoom: this._zoomData
          });
          this.emit('events', {
            type: 'canvas.zoom',
            zoom: this._zoomData
          });
          callback && callback();
        }
        frame++;
      }, time / 20);
    } else {
      callback && callback();
    }
  }
  move(position) {
    $(this.wrapper)
      .css('left', position[0])
      .css('top', position[1]);
    this._coordinateService._changeCanvasInfo({
      canOffsetX: position[0],
      canOffsetY: position[1]
    });
    this._guidelineService.isActive && this._guidelineService.move(position[0], position[1]);
    this._moveData = position;
    this.emit('system.canvas.move');
    this.emit('events', {type: 'system.canvas.move'});
  }
  setGirdMode(flat = true, options = this._bgObj, _isResize) {
    if (flat) {
      this._bgObjQueue.push(options);
      if (this._bgTimer) {
        return;
      }
      this._bgTimer = setInterval(() => {
        if (this._bgObjQueue.length === 0) {
          clearInterval(this._bgTimer);
          this._bgTimer = null;
          return;
        }
        this._bgObj = this._bgObjQueue.pop();
        _isResize && this._gridService._resize();
        this._gridService.create(this._bgObj);
        this._bgObjQueue = [];
      }, 1000);
    } else {
      this._gridService.destroy();
      this._bgObjQueue = [];
    }
  }
  setGuideLine(flat = true, options = this._bgObj) {
    if (flat) {
      this._bgObjQueue.push(options);
      if (this._bgTimer) {
        return;
      }
      this._bgTimer = setInterval(() => {
        if (this._bgObjQueue.length === 0) {
          clearInterval(this._bgTimer);
          this._bgTimer = null;
          return;
        }
        this._bgObj = this._bgObjQueue.pop();
        this._guidelineService.create(this._bgObj);
        this._bgObjQueue = [];
      }, 200);
    } else {
      this._guidelineService.destroy();
      this._bgObjQueue = [];
    }
  }
  canvas2terminal(coordinates, options) {
    return this._coordinateService.canvas2terminal(coordinates, options);
  }
  terminal2canvas(coordinates, options) {
    return this._coordinateService.terminal2canvas(coordinates, options);
  }
  save2img(options) {
    let method = 'toPng';

    switch (options.type) {
      case 'jpeg':
      case '.jpeg':
        method = 'toJpeg';
        break;
      case 'png':
      case '.png':
        method = 'toPng';
        break;
      case 'svg':
      case '.svg':
        method = 'toSvg';
        break;
    }

    return domtoimage[method](this.root, options)
      .then(function (dataUrl) {
        return dataUrl;
      })
      .catch(function (error) {
        console.error('oops, something went wrong!', error);
      });
  }
  justifyCoordinate() {
    this._gridService.justifyAllCoordinate();
  }
  _autoMoveCanvas(x, y, data, cb) {

    if (!this.theme.autoFixCanvas.enable) {
      return;
    }

    this._autoMoveDir = [];
    let _terOffsetX = this._coordinateService.terOffsetX;
    let _terOffsetY = this._coordinateService.terOffsetY;

    clearInterval(this._autoMoveTimer);
    this._autoMoveTimer = null;

    if (this._autoMoveTimer && this._autoMoveDir.length > 0) {
      return;
    }
    let _autoMovePadding = this.theme.autoFixCanvas.autoMovePadding;
    if (x - _terOffsetX <= _autoMovePadding[3]) {
      this._autoMoveDir.push('left');
    }
    if (this._rootWidth - (x - _terOffsetX) <= _autoMovePadding[1]) {
      this._autoMoveDir.push('right');
    }
    if (y - _terOffsetY <= _autoMovePadding[0]) {
      this._autoMoveDir.push('top');
    }
    if (this._rootHeight - (y - _terOffsetY) <= _autoMovePadding[2]) {
      this._autoMoveDir.push('bottom');
    }

    if (this._autoMoveDir.length === 0) {
      clearInterval(this._autoMoveTimer);
      return;
    }

    if (!this._autoMoveTimer) {
      let MOVE_GAP = 5;
      this._autoMoveTimer = setInterval(() => {
        if (this._autoMoveDir.includes('left')) {
          this.move([this._moveData[0] + MOVE_GAP, this._moveData[1]]);
          _moveTarget([-MOVE_GAP, 0]);
        }
        if (this._autoMoveDir.includes('right')) {
          this.move([this._moveData[0] - MOVE_GAP, this._moveData[1]]);
          _moveTarget([+MOVE_GAP, 0]);
        }
        if (this._autoMoveDir.includes('top')) {
          this.move([this._moveData[0], this._moveData[1] + MOVE_GAP]);
          _moveTarget([0, -MOVE_GAP]);
        }
        if (this._autoMoveDir.includes('bottom')) {
          this.move([this._moveData[0], this._moveData[1] - MOVE_GAP]);
          _moveTarget([0, +MOVE_GAP]);
        }
      }, 70);
    }

    // 同时需要移动target
    let _moveTarget = (gap) => {
      if (data.type === 'node:drag') {
        data.nodes.forEach((item) => {
          item.moveTo(item.left + gap[0], item.top + gap[1]);
        })
      } else if (data.type === 'group:drag') {
        let group = data.group;
        group.moveTo(group.left + gap[0], group.top + gap[1]);
      }
      cb && cb(gap);
    }
  }
  undo() {
    let result = [];
    if (this.actionQueueIndex <= -1) {
      console.warn('回退堆栈已空，无法再undo');
      return;
    }
    let step = this.actionQueue[this.actionQueueIndex--];
    if (step.type === '_system:dragNodeEnd') {
      step = this.actionQueue[this.actionQueueIndex--];
    }
    result.push(step);
    if (step.type === 'system:addNodes') {
      this.removeNodes(step.data, false, true);
    } else if (step.type === 'system:removeNodes') {
      this.addNodes(step.data.nodes, true);
      this.addEdges(step.data.edges, true);
    } else if (step.type === 'system:addEdges') {
      this.removeEdges(step.data, true, true);
    } else if (step.type === 'system:removeEdges') {
      this.addEdges(step.data, true);
    } else if (step.type === 'system:moveNodes') {
      for (let key in step.data.nodes) {
        let _nodeInfo = step.data.nodes[key];
        let _node = this.getNode(key);
        _node.moveTo(_nodeInfo.fromLeft, _nodeInfo.fromTop, true);
      }
    } else if (step.type === 'system:moveGroups') {
      for (let key in step.data.groups) {
        let _groupInfo = step.data.groups[key];
        let _group = this.getGroup(key);
        _group.moveTo(_groupInfo.fromLeft, _groupInfo.fromTop, true);
      }
    } else if (step.type === 'system:addGroups') {
      step.data.forEach((item) => {
        if (item.nodes.length > 0) {
          this.removeNodes(item.nodes);
        }
        this.removeGroup(item.group.id, true);
      });
    } else if (step.type === 'system:removeGroup') {
      this.addGroup(step.data.group, step.data.nodes || [], undefined, true);
    } else if (step.type === 'system:groupAddMembers') {
      let sourceGroup = step.data.sourceGroup;
      let targetGroup = step.data.targetGroup;

      if (targetGroup) {
        targetGroup.removeNodes(step.data.nodes, true);
      }

      if (sourceGroup) {
        sourceGroup.addNodes(step.data.nodes, true);
      }

      let _preStep = {};
      if (step.data._isDraging) {
        _preStep = this.actionQueue[this.actionQueueIndex];
        if (_preStep.type === 'system:moveNodes') {
          for (let key in _preStep.data.nodes) {
            let _nodeInfo = _preStep.data.nodes[key];
            let _node = this.getNode(key);
            _node.moveTo(_nodeInfo.fromLeft, _nodeInfo.fromTop, true);
          }
          result.unshift(_preStep);
        }
      }

      this.actionQueueIndex--;

    } else if (step.type === 'system:groupRemoveMembers') {

      let group = step.data.group;

      if (group) {
        group.addNodes(step.data.nodes, true);
      }

      let _preStep = {};
      if (step.data._isDraging) {
        _preStep = this.actionQueue[this.actionQueueIndex];
        if (_preStep.type === 'system:moveNodes') {
          for (let key in _preStep.data.nodes) {
            let _nodeInfo = _preStep.data.nodes[key];
            let _node = this.getNode(key);
            _node.moveTo(_nodeInfo.fromLeft, _nodeInfo.fromTop, true);
          }
          result.unshift(_preStep);
          this.actionQueueIndex--;
        }
      }

      this.actionQueueIndex--;
    } else if (step.type === 'system:reconnectEdges') {

      _.get(step, 'data.info', []).forEach((info) => {
        let targetNode = this.getNode(info.preTargetNodeId);
        let targetEndpoint = targetNode.getEndpoint(info.preTargetPointId);
        info.edge._create({
          id: `${info.edge.sourceEndpoint.id}-${targetEndpoint.id}`,
          targetNode: targetNode,
          _targetType: targetEndpoint.nodeType,
          targetEndpoint: targetEndpoint,
          type: 'endpoint'
        })
      });
    }

    this.emit('system.canvas.undo', {
      steps: result
    });
    this.emit('events', {
      type: 'canvas.undo',
      steps: result
    });
  }
  redo() {
    let result = [];
    if (this.actionQueueIndex >= this.actionQueue.length - 1) {
      console.warn('重做堆栈已到顶，无法再redo');
      return;
    }
    let step = this.actionQueue[++this.actionQueueIndex];
    result.push(step);
    if (step.type === 'system:moveNodes' && step.data._isDraging) {
      step = this.actionQueue[++this.actionQueueIndex];
      result.push(step);
    }
    if (step.type === 'system:addNodes') {
      this.addNodes(step.data, true);
    } else if (step.type === 'system:removeNodes') {
      this.removeNodes(step.data.nodes, false, true);
    } else if (step.type === 'system:addEdges') {
      this.addEdges(step.data, true);
    } else if (step.type === 'system:removeEdges') {
      this.removeEdges(step.data, true);
    } else if (step.type === 'system:moveNodes') {
      for (let key in step.data.nodes) {
        let _nodeInfo = step.data.nodes[key];
        let _node = this.getNode(key);
        _node.moveTo(_nodeInfo.toLeft, _nodeInfo.toTop, true);
      }
    } else if (step.type === 'system:moveGroups') {
      for (let key in step.data.groups) {
        let _groupInfo = step.data.groups[key];
        let _group = this.getGroup(key);
        _group.moveTo(_groupInfo.toLeft, _groupInfo.toTop, true);
      }
    } else if (step.type === 'system:addGroups') {
      step.data.forEach((item) => {
        this.addGroup(item.group, item.nodes || [], undefined, true);
      })
    } else if (step.type === 'system:removeGroup') {
      this.removeGroup(step.data.group, true);
    } else if (step.type === 'system:groupAddMembers') {
      let sourceGroup = step.data.sourceGroup;
      let targetGroup = step.data.targetGroup;

      let _preStep = this.actionQueue[this.actionQueueIndex - 1];

      if (_preStep.type === 'system:moveNodes' && _preStep.data._isDraging) {
        for (let key in _preStep.data.nodes) {
          let _nodeInfo = _preStep.data.nodes[key];
          let _node = this.getNode(key);
          _node.moveTo(_nodeInfo.toLeft, _nodeInfo.toTop, true);
        }
      }

      if (targetGroup) {
        targetGroup.addNodes(step.data.nodes, true);
      }

      if (sourceGroup) {
        sourceGroup.removeNodes(step.data.nodes, true);
      }

    } else if (step.type === 'system:groupRemoveMembers') {
      let group = step.data.group;

      if (group) {
        group.removeNodes(step.data.nodes, true);
      }

      let _preStep = {};
      if (step.data._isDraging) {
        _preStep = this.actionQueue[this.actionQueueIndex];
        if (_preStep.type === 'system:moveNodes') {
          for (let key in _preStep.data.nodes) {
            let _nodeInfo = _preStep.data.nodes[key];
            let _node = this.getNode(key);
            _node.moveTo(_nodeInfo.fromLeft, _nodeInfo.fromTop, true);
          }
          this.actionQueueIndex--;
        }
      }
    } else if (step.type === 'system:reconnectEdges') {
      _.get(step, 'data.info', []).forEach((info) => {
        let targetNode = this.getNode(info.currentTargetNodeId);
        let targetEndpoint = targetNode.getEndpoint(info.currentTargetPointId);
        info.edge._create({
          id: `${info.edge.sourceEndpoint.id}-${targetEndpoint.id}`,
          targetNode: targetNode,
          _targetType: targetEndpoint.nodeType,
          targetEndpoint: targetEndpoint,
          type: 'endpoint'
        })
      });
    }

    this.emit('system.canvas.redo', {
      steps: result
    });
    this.emit('events', {
      type: 'canvas.redo',
      steps: result
    });

    if (_.get(this.actionQueue, [this.actionQueueIndex + 1, 'type']) === '_system:dragNodeEnd') {
      this.actionQueueIndex++;
    }
  }
  isActionQueueTop() {
    return this.actionQueueIndex >= this.actionQueue.length - 1;
  }
  isActionQueueBottom() {
    return this.actionQueueIndex <= -1;
  }
  pushActionQueue(option) {

    let step = option;
    //移动节点需要合并堆栈
    if (option.type === 'system:moveNodes' || option.type === 'system:moveGroups') {

      let _type = {
        'system:moveNodes': 'node',
        'system:moveGroups': 'group'
      }[option.type];
      let _types = _type + 's';

      // 堆栈前一个不是moveNode
      let currentStep = this.actionQueue[this.actionQueueIndex] || {};
      if (currentStep.type === option.type && currentStep.data[_types][option.data[_type].id]) {
        currentStep.data[_types][option.data[_type].id]['toTop'] = option.data.top;
        currentStep.data[_types][option.data[_type].id]['toLeft'] = option.data.left;
        return;
      } else {
        let moveItems = [option.data[_type]];
        const unionKeys = this._findUnion(_types, option.data[_type]);
        if (unionKeys && unionKeys.length > 0) {
          unionKeys.forEach((key) => {
            moveItems = moveItems.concat(this._unionData[key][_types]);
          });
          moveItems = _.uniqBy(moveItems, 'id');
        }

        step = {
          type: option.type,
          data: {
            [_types]: {}
          }
        };

        moveItems.forEach((item) => {
          step.data[_types][item.id] = {
            fromTop: item.top,
            fromLeft: item.left,
            toTop: item.top,
            toLeft: item.left
          }
        });
        step.data[_types][option.data[_type].id]['toTop'] = option.data.top;
        step.data[_types][option.data[_type].id]['toLeft'] = option.data.left;
      }
    }

    // 堆栈满了，清理
    if (this.actionQueueIndex >= this.global.limitQueueLen) {
      this.actionQueue.shift();
      this.actionQueueIndex--;
    }
    // 把index前的步骤覆盖掉
    this.actionQueue.splice(this.actionQueueIndex + 1, this.actionQueue.length); // todo可能有问题
    this.actionQueue.push(step);
    this.actionQueueIndex++;

    if (_.get(this.actionQueue, [this.actionQueueIndex - 1, 'type']) === '_system:dragNodeEnd') {
      this.actionQueue.splice(this.actionQueueIndex - 1, 1);
      this.actionQueueIndex--;
    }
  }
  popActionQueue() {
    if (this.actionQueue.length > 0) {
      let action = this.actionQueue.pop();
      return action;
    } else {
      console.warn('操作队列已为空，请确认');
    }
  }
  clearActionQueue() {
    this.actionQueue = [];
    this.actionQueueIndex = -1;
  }
}

export default BaseCanvas;
