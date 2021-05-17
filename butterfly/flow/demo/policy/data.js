'use strict';

import Node from './node';
import Edge from './edge';
import BaseNode from "../entity-relationship/node";
const data = {
  nodes: [
    {
      id: '1',
      label: '开始',
      className: 'icon-background-color',
      iconType: 'icon-bofang',
      nodeType:'begin',
      top: 405,
      left: 400,
      Class: Node,
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
    },
    {
      id: '2',
      label: '默认通过',
      className: 'icon-background-color',
      iconType: 'icon-rds',
      top: 405,
      left: 540,
      Class: Node,
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
    },
    {
      id: '3',
      text: '条件节点',
      top: 388,
      left: 740,
      shape: 'diamond',
      className: 'icon-background-color',
      iconType: 'icon-rds',
      Class: BaseNode,
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
  ],
  edges: [
    {
      source: 'right',
      target: 'left',
      sourceNode: '1',
      targetNode: '2',
      arrow: true,
      type: 'endpoint',
      arrowPosition: 0.5,
      Class: Edge
    },
    {
      source: 'right',
      target: 'left',
      sourceNode: '2',
      targetNode: '3',
      arrow: true,
      type: 'endpoint',
      arrowPosition: 0.5,
      Class: Edge
    }
  ]
};
export default data;
