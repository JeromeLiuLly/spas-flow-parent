'use strict';

import BaseNode from './node';
import BaseEdge from './edge';
const data = {
  nodes: [{
      id: '1',
      text: 'Gets Paid',
      top: 175,
      left: 600,
      shape: 'diamond',
      Class: BaseNode,
      width: 128,
      height: 61,
      fill: '#776ef3',
      rectDasharray: 'none'
    }, {
      id: '2',
      text: 'Employee',
      top: 185,
      left: 300,
      color: 'box-background-color',
      shape: 'rect',
      Class: BaseNode,
    }, {
      id: '3',
      text: 'Number',
      top: 90,
      left: 200,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
    }, {
      id: '4',
      text: 'Name',
      top: 30,
      left: 325,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
    }, {
      id: '5',
      text: 'Skills',
      top: 90,
      left: 450,
      width: 95,
      height: 45,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
      // fill: '#FFA940',
      // ellipseBorderWidth: 2,
      // ellipseDasharray: 'none'
    }
    , {
      id: '6',
      text: 'ISA',
      top: 280,
      left: 320,
      width: 100,
      height: 50,
      shape: 'triangle',
      fill: '#fff',
      Class: BaseNode,
    }, {
      id: '7',
      text: 'Salesman',
      top: 370,
      left: 300,
      color: 'box-background-color',
      shape: 'rect',
      Class: BaseNode,
    }, {
      id: '8',
      text: 'Uses',
      top: 360,
      left: 600,
      shape: 'diamond',
      Class: BaseNode,
      width: 70,
      height: 70,
      fill: '#797d9a',
    } , {
      id: '9',
      text: 'Company car',
      top: 370,
      left: 900,
      color: 'box-background-color',
      shape: 'rect',
      Class: BaseNode,
    } , {
      id: '10',
      text: 'Plate',
      top: 370,
      left: 1100,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
    },
    {
      id: '11',
      text: 'Wage',
      top: 185,
      left: 900,
      color: 'box-background-color',
      shape: 'rect',
      Class: BaseNode,
      width: 137,
      height: 42,
      fill: '#31d0c6',
      rectDasharray: 'none'
    }, {
      id: '12',
      text: 'Amount',
      top: 90,
      left: 1040,
      width: 95,
      height: 45,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
      // fill: '#FFA940',
      // ellipseBorderWidth: 2,
      // ellipseDasharray: '3 1'
    }, , {
      id: '13',
      text: 'Date',
      top: 90,
      left: 810,
      color: 'box-background-color',
      shape: 'ellipse',
      Class: BaseNode,
    }
  ],
  edges: [{
      source: '1',
      target: '2',
      type: 'node',
      label: '1',
      Class: BaseEdge
  },
   {
      source: '2',
      target: '3',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '2',
      target: '4',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '2',
      target: '5',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '2',
      target: '6',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '6',
      target: '7',
      type: 'node',
      Class: BaseEdge
    },{
      source: '7',
      target: '8',
      type: 'node',
      label: '0..1',
      Class: BaseEdge
    }, {
      source: '8',
      target: '9',
      type: 'node',
      label: '1..1',
      Class: BaseEdge
    }, {
      source: '9',
      target: '10',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '1',
      target: '11',
      type: 'node',
      label: 'N',
      Class: BaseEdge
    } , {
      source: '11',
      target: '12',
      type: 'node',
      Class: BaseEdge
    }, {
      source: '11',
      target: '13',
      type: 'node',
      Class: BaseEdge
    },
],
  groupd: []
}

export default data;