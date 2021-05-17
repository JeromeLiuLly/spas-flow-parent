'use strict';

const $ = require('jquery');

class SelectCanvas {
  constructor() {
    this.dom = $('<canvas class="butterfly-selected-canvas"></canvas>')[0];
    this.cxt = this.dom.getContext('2d');
    this.canvasTop = 0;
    this.canvasLeft = 0;
    this.canvasHeight = 0;
    this.canvasWidth = 0;
    this.canScrollX = 0;
    this.canScrollY = 0;
    this.startX = 0;
    this.startY = 0;
    this.endX = 0;
    this.endY = 0;
    this._on = null;
    this._emit = null;
    this.isDraw = false;
    this.isActive = false;
  }

  init(opts) {
    const root = opts.root;
    this.resize(opts);
    this.addEventListener();
    this._on = opts._on;
    this._emit = opts._emit;
    $(this.dom).appendTo(root);
  }

  resize(opts) {
    const root = opts.root;
    const offset = $(root).offset();
    this.canvasTop = offset.top;
    this.canvasLeft = offset.left;
    this.canvasHeight = $(root).height();
    this.canvasWidth = $(root).width();
    $(this.dom).attr('width', this.canvasWidth);
    $(this.dom).attr('height', this.canvasHeight);
  }

  addEventListener() {
    this.dom.addEventListener('mousedown', this.mouseDown.bind(this));
    this.dom.addEventListener('mouseup', this.mouseUp.bind(this));
    this.dom.addEventListener('mousemove', this.mouseMove.bind(this));
    this.dom.addEventListener('mouseleave', this.mouseLeave.bind(this));
  }

  mouseDown(evt) {
    this.startX = evt.clientX;
    this.startY = evt.clientY;
    this.isDraw = true;
  }

  mouseUp(evt) {
    this.isDraw = false;
    this.clearCanvas();
    const startX = this.startX;
    const startY = this.startY;
    const endX = this.endX = evt.clientX;
    const endY = this.endY = evt.clientY;
    const toDirection = endX - startX > 0 ? 'right' : 'left'

    const startLeft = startX > endX ? endX : startX;
    const startTop = startY > endY ? endY : startY;
    const endLeft = startX > endX ? startX : endX;
    const endTop = startY > endY ? startY : endY;


    this._emit('InnerEvents', {
      type: 'multiple:select',
      range: [startLeft, startTop, endLeft, endTop],
      toDirection
    });

    this.unActive();
  }

  mouseMove(evt) {
    if (!this.isDraw) {
      return;
    }

    this.endX = evt.clientX;
    this.endY = evt.clientY;
    this.drawRect();
  }

  mouseLeave(evt) {
    this.isDraw = false;
    this.clearCanvas();
  }

  drawRect() {
    if (!this.isDraw) {
      return;
    }
    this.clearCanvas();
    const startX = Math.min(this.startX, this.endX) - this.canvasLeft + this.canScrollX;
    const startY = Math.min(this.startY, this.endY) - this.canvasTop + this.canScrollY;
    const width = Math.abs(this.startX - this.endX);
    const height = Math.abs(this.startY - this.endY);

    this.cxt.beginPath();
    this.cxt.rect(startX, startY, width, height);
    this.cxt.fillStyle = '#b3dbff';
    this.cxt.fill();
    this.cxt.lineWidth = '1';
    this.cxt.strokeStyle = '#3da4ff';
    this.cxt.stroke();
  }

  clearCanvas() {
    this.cxt.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
  }

  getCanvas() {
    return this.dom;
  }

  active() {
    if (!this.isActive) {
      $(this.dom).addClass('wrapper-up');
      this.isActive = true;
    }
  }

  unActive() {
    if (this.isActive) {
      $(this.dom).removeClass('wrapper-up');
      this.isActive = false;
    }
  }

  _changeCanvasInfo(data) {
    if (data.terScrollX !== undefined) {
      this.canScrollX = data.terScrollX;
    }
    if (data.terScrollY !== undefined) {
      this.canScrollY = data.terScrollY;
    }
  }
}

export default SelectCanvas;
