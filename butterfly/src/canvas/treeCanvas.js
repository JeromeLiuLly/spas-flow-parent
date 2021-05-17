'use strict';

import Canvas from "./baseCanvas";
const _ = require('lodash');
import TreeNode from '../node/treeNode';
import Node from '../node/baseNode';
import Hierarchy from '../utils/layout/hierarchy';

class TreeCanvas extends Canvas {
  constructor(options) {
    super(options);
    this._NodeClass = TreeNode;
  }
  _handleTreeNodes(data, isFlat) {
    if (isFlat) {
      return data;
    } else {
      let queue = [data];
      let nodeList = [];
      while (queue.length > 0) {
        let node = queue.pop();
        nodeList.push(node);
        if (node.children && node.children.length > 0) {
          node.children.forEach((child) => {
            child.parent = node.id;
          });
          queue = queue.concat(node.children);
        }
      }
      return nodeList;
    }
  }
  _isExistNode(node) {
    let result = super._isExistNode(node);
    if (result && node.subCollapsed) {
      result = false;
    }
    return;
  }
  _addEventListener() {
    super._addEventListener();
    this.on('InnerEvents', (data) => {
      if (data.type === 'node:collapse') {
        this.collapseNode(data.nodeId);
      } else if (data.type == 'node:expand') {
        this.expandNode(data.nodeId);
      }
    });
  }
  findSubTree(item) {
    let queue = [item];
    let result = [];
    while (queue.length > 0) {
      let _item = queue.pop();
      result.push(_item);
      if (_item.children?.length > 0) {
        queue = queue.concat(_item.children);
      }
    }
    return result;
  }
  collapseNode (nodeId) {
    let collapseNodes = [];
    let collapseEdges = [];
    let targetNode = this.getNode(nodeId);
    targetNode.collapsed = true;
    targetNode.options.collapsed = true;
    if (!targetNode) {
      return;
    }
    let queue = [targetNode];
    while (queue.length > 0) {
      let node = queue.pop();
      collapseNodes.push(node);
      if (node.children && node.children.length > 0) {
        queue = queue.concat(node.children);
      }
    }
    // 先去掉target node做filter
    collapseNodes = collapseNodes.filter((item) => {
      return item.id !== targetNode.id;
    });
    let tmp = {};
    collapseNodes.forEach((item) => {
      tmp[item.id] = item;
      item.subCollapsed = true;
    });
    collapseEdges = this.edges.filter((item) => {
      let _isCollapsed = false;
      if (item.type === 'endpoint') {
        _isCollapsed = !!tmp[item.sourceNode.id] || !!tmp[item.targetNode.id]
      } else {
        _isCollapsed = !!tmp[item.source.id] || !!tmp[item.target.id];
      }
      if (item.sourceNode.id === targetNode.id) {
        _isCollapsed = true;
      }

      if (_isCollapsed) {
        item.collapsed = true;
      }
      return _isCollapsed;
    });
    collapseNodes.unshift(targetNode);

    collapseNodes.forEach((item) => {
      if (item.subCollapsed) {
        item.destroy(true);
      }

      // 重置子节点的collapsed状态
      if (item.id !== nodeId && item.collapsed) {
        delete item.collapsed;
      }
    });
    collapseEdges.forEach((item) => {
      item.destroy(true);
    });

    this.redraw();

    this.emit('system.node.collapse', {
      target: targetNode,
      nodes: collapseNodes,
      edges: collapseEdges
    });
    this.emit('events', {
      type: 'node.collapse',
      target: targetNode,
      nodes: collapseNodes,
      edges: collapseEdges
    });

    return {
      nodes: collapseNodes,
      edges: collapseEdges
    }
  }

  expandNode(nodeId, nodes) {
    let targetNode = this.getNode(nodeId);
    let subNodes = [];
    let collapseEdges = [];
    let queue = [targetNode];
    while (queue.length > 0) {
      let node = queue.pop();
      subNodes.push(node);
      if (node.children && node.children.length > 0) {
        queue = queue.concat(node.children);
      }
    }
    // 先去掉target node做filter
    subNodes = subNodes.filter((item) => {
      return item.id !== targetNode.id;
    });
    let tmp = {};
    subNodes.forEach((item) => {
      tmp[item.id] = item;
      item.subCollapsed = true;
    });
    collapseEdges = this.edges.filter((item) => {
      let _isCollapsed = false;
      if (item.type === 'endpoint') {
        _isCollapsed = !!tmp[item.sourceNode.id] || !!tmp[item.targetNode.id]
      } else {
        _isCollapsed = !!tmp[item.source.id] || !!tmp[item.target.id];
      }
      if (item.sourceNode.id === targetNode.id) {
        _isCollapsed = true;
      }
      return _isCollapsed;
    });
    this.nodes = _.differenceBy(this.nodes, subNodes, 'id');
    let addNodes = this.addNodes(subNodes, true);
    this.edges = _.filter(this.edges, (a) => {
      if (a.type === 'endpoint') {
        return !_.some(collapseEdges, ((b) => {
          return a.sourceNode.id === b.sourceNode.id && a.targetNode.id === b.targetNode.id && a.source === b.source && a.target === b.target;
        }));
      } else {
        return !_.some(collapseEdges, ((b) => {
          return a.source === b.source && a.target === b.target;
        }));
      }
    });
    let addEdges = this.addEdges(collapseEdges, true);
    subNodes.forEach((item) => {
      delete item.subCollapsed;
    });
    delete targetNode.collapsed;
    collapseEdges.forEach((item) => {
      delete item.collapsed;
    });
    this.redraw();
    this.emit('system.node.expand', {
      target: targetNode,
      nodes: addNodes,
      edges: addEdges
    });
    this.emit('events', {
      type: 'node.expand',
      target: targetNode,
      nodes: addNodes,
      edges: addEdges
    });
  }

  redraw() {
    let rootNode = this.getRootNode();
    let tree = [];
    let tmpTreeObj = {};
    let queue = [rootNode];
    while (queue.length > 0) {
      let node = queue.pop();
      let obj = {
        id: node.id
      };
      if (tmpTreeObj[node.id]) {
        obj = tmpTreeObj[node.id];
      } else {
        tmpTreeObj[obj.id] = obj;
      }
      if (node.isRoot) {
        obj['isRoot'] = node.isRoot;
      }
      if (node.collapsed) {
        obj['collapsed'] = node.collapsed;
      }
      tree.push(obj);
      if (node.children && node.children.length > 0) {
        obj.children = [];
        node.children.forEach((child) => {
          let _childObj = {
            id: child.id
          };
          if (tmpTreeObj[child.id]) {
            _childObj = tmpTreeObj[child.id];
          } else {
            tmpTreeObj[child.id] = _childObj;
          }
          if (child.isRoot) {
            _childObj['isRoot'] = child.isRoot;
          }
          if (child.collapsed) {
            _childObj['collapsed'] = child.collapsed;
          }
          obj.children.push(_childObj);
        });
        queue = queue.concat(node.children);
      }
    }
    let nodes = tree.filter((item) => {
      return true;
    });
    this._autoLayout({
      nodes: nodes,
      edges: [],
      groups: []
    });
    this.nodes.forEach((item) => {
      if (item.subCollapsed) {
        return;
      }
      item?.endpoints?.forEach(endpoint => {
        endpoint.updatePos();
      });
      let obj = tmpTreeObj[item.id];
      if (item.top !== obj.top || item.left !== obj.left) {
        item.options.top = obj.top;
        item.options.left = obj.left;
        item.options.treePos = obj.treePos;
        item.moveTo(obj.left, obj.top);
      }
    });
    this.edges.forEach((item) => {
      item.redraw();
    });
    this.emit('system.canvas.redraw');
    this.emit('events', {
      type: 'canvas:redraw'
    });
  }
  addNodes(data, isNotEventEmit) {
    let nodes = super.addNodes(data, isNotEventEmit);
    nodes.forEach((item) => {
      if(item.parent) {
        let parentNode = this.getNode(item.parent);
        if (!_.some(parentNode.children, ['id', item.id])) {
          !parentNode.children && (parentNode.children = []);
          parentNode.children.push(item);
        }
      }
    });
    return nodes;
  }
  removeNodes(data, isNotDelEdge, isNotEventEmit) {
    let nodes = data.map((item) => {
      if (item instanceof Node) {
        return item;
      } else {
        return this.getNode(item);
      }
    });

    let rmNodes = [];

    nodes.forEach((item) => {
      let _subTree = this.findSubTree(item);
      rmNodes = rmNodes.concat(_subTree);
      // 如果是某个节点的子节点,将此节点从父节点的children中移除
      if (item.parent) {
        const parentNode = this.getNode(item.parent);
        if (parentNode) {
          parentNode.children = parentNode.children.filter((node) => node.id !== item.id);
        }
      }
    });

    rmNodes = _.unionBy(rmNodes, 'id');

    let result = super.removeNodes(rmNodes, isNotDelEdge, isNotEventEmit);

    return result;
  }
  getRootNode() {
    return this.nodes.filter((item) => {
      return item.isRoot;
    })[0];
  }
  draw(opts, params, callback) {
    const nodes = this._handleTreeNodes(opts.nodes || [], _.get(params, 'isFlatNode', false));
    // 需要过滤掉collapsed的
    super.draw({
      nodes: nodes,
      edges: opts.edges,
      groups: opts.groups
    }, () => {
      let tmp = {};
      this.nodes.forEach((item) => {
        tmp[item.id] = item;
        item.children = [];
      });
      this.nodes.forEach((item) => {
        if (item.isRoot || !item.parent) {
          return;
        }
        let parentNode = tmp[item.parent];
        if (!parentNode) {
          return;
        }
        !parentNode.children && (parentNode.children = []);
        parentNode.children.unshift(item);
      });
      callback && callback({
        nodes: this.nodes,
        edges: this.edges,
        groups: this.groups
      });
    });
  }
  // getDataMap(isFlat) {

  // }
  _autoLayout(options) {
    let rootNode = options.nodes.filter((item) => {
      return item.isRoot;
    })[0];
    if (!rootNode) {
      return;
    }

    // 这部分需要优化
    let type = _.get(this, 'layout.type', 'compactBox');
    if (Hierarchy[type]) {
      const NODE_SIZE = 16;
      const NODE_WIDTH = 30;
      const PEM = 5;
      Hierarchy[type](rootNode, _.assign({
        direction: 'H',
        getId(d) {
          return d.id;
        },
        getHeight(d) {
          if (d.isRoot) {
            return NODE_SIZE * 2;
          }
          return NODE_SIZE;
        },
        getWidth(d) {
          if (d.isRoot) {
            return NODE_WIDTH * 2;
          }
          return NODE_WIDTH;
        },
        getHGap(d) {
          return 100;
        },
        getVGap(d) {
          return 30;
        },
        getSubTreeSep(d) {
          if (!d.children || !d.children.length) {
            return 0;
          }
          return PEM;
        }
      }, _.get(this, 'layout.options', {})));
    }
  }
}

export default TreeCanvas;