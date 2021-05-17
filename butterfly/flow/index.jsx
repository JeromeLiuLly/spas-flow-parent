'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import {BrowserRouter as Router, Route, Link, Redirect} from 'react-router-dom';
import {Layout, Menu} from 'antd';

import Policy from './demo/policy/index.jsx';

import 'antd/dist/antd.css';
import './static/iconfont.css';
import './static/newIconfont.css';
import './index.less';

const {Header, Content, Sider} = Layout;

ReactDOM.render((
  <Router>
    <Layout>
      <Header className='header'>可视化工作流编排</Header>
      <Layout>
        <Content>
          <Route path="/policy" component={Policy} />
          <Route exact path="/" component={() => <Redirect exact from="/" to="/policy" />} />
        </Content>
      </Layout>
    </Layout>
  </Router>
), document.getElementById('main'));
