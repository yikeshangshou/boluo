<%@ page language="java" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/commons/taglibs.jsp"%>
<div class="container row-space-top-2">
  <ol class="breadcrumb">
    <li class="active">系统设置</li>
  </ol>
  <div class="row">
    <div class="col-md-12">
      <table class="table table-bordered table-condensed table-hover" id="setting-list">
        <thead>
          <tr>
            <th>系统参数</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr data-name="section_empty_switch">
            <td>当某个时间区间内资讯列表为空时，是否显示这个区间？</td>
            <td><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>／<span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>