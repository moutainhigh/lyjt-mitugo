<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!-- 搜索 -->
      <el-input v-model.trim="query.value" clearable placeholder="输入搜索内容" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
      <el-select v-model="query.type" clearable placeholder="类型" class="filter-item" style="width: 130px">
        <el-option v-for="item in queryTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
      </el-select>
<!--      <el-input v-model="query.phone" clearable placeholder="用户手机号" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />-->
<!--      <el-input v-model="query.userTrueName" clearable placeholder="真实姓名" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />-->
<!--      <el-input v-model="query.bankCode" clearable placeholder="银行卡号" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />-->
<!--      <el-input v-model="query.seqNo" clearable placeholder="订单号" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />-->
      <el-select v-model="query.userType" clearable placeholder="用户类型"
                 style="width: 200px;" class="filter-item">
        <el-option
          v-for="(item,index) in userTypeOptions"
          :key="index"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-select v-model="query.status" clearable placeholder="审核状态"
                 style="width: 200px;" class="filter-item">
        <el-option
          v-for="item in statusList"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-date-picker
        v-model="query.addTime"
        :default-time="['00:00:00','23:59:59']"
        type="daterange"
        range-separator=":"
        size="small"
        class="date-item"
        value-format="yyyy-MM-dd"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
      />
      <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="toQuery">搜索</el-button>
      <!-- 新增 -->
      <el-button
        type="danger"
        class="filter-item"
        size="mini"
        icon="el-icon-refresh"
        @click="toQuery"
      >刷新</el-button>
      <div>
        <el-button
          :loading="downloadLoading"
          class="filter-item"
          size="mini"
          type="warning"
          icon="el-icon-download"
          @click="downloadMethod()"
        >导出</el-button>
      </div>
    </div>
    <!--表单组件-->
    <eForm ref="form" :is-add="isAdd" />
    <!--表格渲染-->
    <el-table v-loading="loading" :data="data" size="small" style="width: 100%;">
      <el-table-column prop="id" label="申请ID" />
      <el-table-column prop="realName" label="用户名" />
      <el-table-column prop="userTrueName" label="真实姓名" />
      <el-table-column prop="bankMobile" label="用户手机号" />
      <el-table-column prop="bankCode" label="银行卡号" />
      <el-table-column prop="extractPrice" label="提现金额" />
      <el-table-column prop="seqNo" label="流水号" />
      <el-table-column prop="userType" label="用户类型" >
        <template slot-scope="scope">
          <div>{{ transferLabel(scope.row.userType,userTypeOptions) }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="extractType" label="提现方式">
        <template slot-scope="scope">
          <div v-if="scope.row.extractType=='wx'">微信</div>
          <div v-else-if="scope.row.extractType=='alipay'">支付宝</div>
          <div v-else-if="scope.row.extractType=='bank'">银行卡</div>
          <div v-else></div>
        </template>
      </el-table-column>
      <el-table-column prop="addTime" label="添加时间">
        <template slot-scope="scope">
          <span>{{ formatTimeTwo(scope.row.addTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="审核状态">
        <template slot-scope="scope">
          <div v-if="scope.row.status==1">
            {{ transferLabel(scope.row.status,statusList) }}
          </div>
          <div v-else-if="scope.row.status==-1">
            {{ transferLabel(scope.row.status,statusList) }}<br>
            未通过原因：{{ scope.row.failMsg }}
            <br>
            未通过时间：{{ formatTimeTwo(scope.row.failTime) }}
          </div>
          <div v-else>
            {{ transferLabel(scope.row.status,statusList) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="checkPermission(['admin','YXUSEREXTRACT_ALL','YXUSEREXTRACT_EDIT','YXUSEREXTRACT_DELETE'])" label="操作" width="150px" align="center">
        <template slot-scope="scope">
          <el-button
            v-if='scope.row.status!==1 && scope.row.status!==-1'
            v-permission="['admin','YXUSEREXTRACT_ALL','YXUSEREXTRACT_EDIT']"
            size="mini"
            type="primary"
            @click="edit(scope.row)"
          >操作</el-button>
          <!--<el-popover-->
          <!--v-permission="['admin','YXUSEREXTRACT_ALL','YXUSEREXTRACT_DELETE']"-->
          <!--:ref="scope.row.id"-->
          <!--placement="top"-->
          <!--width="180">-->
          <!--<p>确定删除本条数据吗？</p>-->
          <!--<div style="text-align: right; margin: 0">-->
          <!--<el-button size="mini" type="text" @click="$refs[scope.row.id].doClose()">取消</el-button>-->
          <!--<el-button :loading="delLoading" type="primary" size="mini" @click="subDelete(scope.row.id)">确定</el-button>-->
          <!--</div>-->
          <!--<el-button slot="reference" type="danger" icon="el-icon-delete" size="mini"/>-->
          <!--</el-popover>-->
        </template>
      </el-table-column>
    </el-table>
    <!--分页组件-->
    <el-pagination
      :total="total"
      :current-page="page + 1"
      style="margin-top: 8px;"
      layout="total, prev, pager, next, sizes"
      @size-change="sizeChange"
      @current-change="pageChange"
    />
  </div>
</template>

<script>
import checkPermission from '@/utils/permission'
import initData from '@/mixins/crud'
import { del } from '@/api/yxUserExtract'
import eForm from './form'
import { formatTimeTwo } from '@/utils/index'
export default {
  components: { eForm },
  mixins: [initData],
  data() {
    return {
      delLoading: false,
      queryTypeOptions: [
        { key: 'realName', display_name: '用户名' },
        { key: 'phone', display_name: '用户手机号' },
        { key: 'userTrueName', display_name: '真实姓名' },
        { key: 'bankCode', display_name: '银行卡号' },
        { key: 'seqNo', display_name: '流水号' }
      ],
      userTypeOptions: [
        {value:1,label:'商户'},
        {value:2,label:'合伙人'},
        {value:3,label:'用户'},
        {value:0,label:'预留'}
      ],
      statusList:[
        {value:-1,label:'提现未通过'},
        {value:0,label:'未提现'},
        {value:1,label:'提现通过'},
        {value:2,label:'提现处理中'},
        {value:3,label:'提现失败'}
      ]
    }
  },
  created() {
    this.$nextTick(() => {
      this.init()
    })
  },
  computed:{
    transferLabel:function(){
      return function(value,list){
        if(list.length){
          let i= list.filter(function(item){
            return new RegExp(item.value, 'i').test(value)
          })
          if(i.length){
            return i[0].label
          }else{
            return ""
          }
        }else{
          return ""
        }
      }
    }
  },
  methods: {
    formatTimeTwo,
    checkPermission,
    beforeInit() {
      this.url = 'api/yxUserExtract'
      const sort = 'id,desc'
      this.params = { page: this.page, size: this.size, sort: sort }
      const query = this.query
      const type = query.type
      const value = query.value
      if (type && value) { this.params[type] = value }
      return true
    },
    subDelete(id) {
      this.delLoading = true
      del(id).then(res => {
        this.delLoading = false
        this.$refs[id].doClose()
        this.dleChangePage()
        this.init()
        this.$notify({
          title: '删除成功',
          type: 'success',
          duration: 2500
        })
      }).catch(err => {
        this.delLoading = false
        this.$refs[id].doClose()
        console.log(err.response.data.message)
      })
    },
    add() {
      this.isAdd = true
      this.$refs.form.dialog = true
    },
    edit(data) {
      this.isAdd = false
      const _this = this.$refs.form
      _this.form = {
        id: data.id,
        uid: data.uid,
        realName: data.realName,
        extractType: data.extractType,
        bankCode: data.bankCode,
        bankAddress: data.bankAddress,
        alipayCode: data.alipayCode,
        bankMobile: data.bankMobile,
        extractPrice: data.extractPrice,
        mark: data.mark,
        balance: data.balance,
        failMsg: data.failMsg,
        failTime: data.failTime,
        addTime: data.addTime,
        status: data.status,
        wechat: data.wechat
      }
      _this.dialog = true
    }
  }
}
</script>

<style scoped>

</style>
