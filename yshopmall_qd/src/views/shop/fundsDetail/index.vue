<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <el-input v-model.trim="query.title" clearable placeholder="输入标题" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
       <el-input v-model.trim="query.username" clearable placeholder="输入用户昵称" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
      <!-- <el-select v-model="query.category" clearable placeholder="明细种类" class="filter-item" style="width: 130px">
        <el-option
          v-for="item in categoryOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select> -->
      <el-input v-model.trim="query.phone" clearable placeholder="输入用户手机号" style="width: 200px;" class="filter-item" @keyup.enter.native="pageRefesh" />
      <el-select v-model="query.userType" clearable placeholder="用户类型" class="filter-item" style="width: 130px">
        <template>
          <el-option
            v-for="item in userTypes"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </template>
      </el-select>

      <el-input v-model.trim="query.linkId" clearable placeholder="输入订单号" style="width: 200px;" class="filter-item" @keyup.enter.native="pageRefesh" />
      <el-select v-model="query.type" clearable placeholder="明细类型" class="filter-item" style="width: 130px">
        <template v-for="item in typeOptions">
        <el-option
          v-for="(val,key) in item"
          :key="key"
          :label="val"
          :value="key"
        />
        </template>
      </el-select>
      <el-select v-model="query.pm" clearable placeholder="收支类型" class="filter-item" style="width: 130px">
        <el-option
          v-for="item in pmOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-date-picker
          type="daterange"
          v-model="searchTime"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          placeholder="选择时间范围"
          value-format='yyyy-MM-dd'
          style="verticalAlign:top;marginRight:20px;"
          @change="(val)=>{if(val){query.addTimeStart=val[0];query.addTimeEnd=val[1]}else{query.addTimeStart=null;query.addTimeEnd=null}}"
          >
        </el-date-picker>
      <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="crud.toQuery">搜索</el-button>
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
<!--       <crudOperation :permission="permission" />-->
      <div>
        <el-button
          :loading="crud.downloadLoading"
          class="filter-item"
          size="mini"
          type="warning"
          icon="el-icon-download"
          @click="doExport()"
          v-permission='permission.download'
        >导出</el-button>
      </div>
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="500px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="主键">
            <el-input v-model="form.id" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="明细种类" prop="type">
            <el-input v-model="form.type" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="用户uid">
            <el-input v-model="form.uid" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="form.username" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="订单号" prop="linkId">
            <el-input v-model="form.linkId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="收支类型" prop="pm">
            <el-input v-model="form.pm" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="订单金额" prop="number">
            <el-input v-model="form.number" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="订单日期" prop="addTime">
            <el-input v-model="form.addTime" style="width: 370px;" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
        <el-table-column type="selection" width="55" />
        <el-table-column v-if="columns.visible('uid')" prop="uid" label="用户uid" />
        <el-table-column v-if="columns.visible('username')" prop="username" label="用户名" />
        <el-table-column v-if="columns.visible('phone')" prop="phone" label="用户手机号" />
        <el-table-column v-if="columns.visible('title')" prop="title" label="标题" />
        <el-table-column prop="category" label="明细种类">
          <template slot-scope="scope">
            <span v-if="scope.row.category == 'now_money'">余额</span>
            <span v-else-if="scope.row.category == 'integral'">积分</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="明细类型">
          <template slot-scope="scope">
            {{
              typeLabel(scope.row.type)
            }}
          </template>
        </el-table-column>
        <el-table-column v-if="columns.visible('linkId')" prop="linkId" label="订单号" />
        <el-table-column v-if="columns.visible('pm')" prop="pm" label="收支类型" >
        <template slot-scope="scope">
          <span v-if="scope.row.pm == '0'">支出</span>
          <span v-else-if="scope.row.pm == '1'">收入</span>
          <span v-else>未知</span>
        </template>
      </el-table-column>
        <el-table-column v-if="columns.visible('userType')" prop="userType" label="用户类型" >
          <template slot-scope="scope">
            {{ transferLabel(scope.row.userType,userTypes) }}
          </template>
        </el-table-column>
        <el-table-column v-if="columns.visible('number')" prop="number" label="金额" />
        <el-table-column v-if="columns.visible('addTime')" prop="addTime" label="订单日期">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.addTime) }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column v-permission="['admin','fundsDetail:edit','fundsDetail:del']" label="操作" width="150px" align="center">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column> -->
      </el-table>
      <!--分页组件-->
      <pagination />
    </div>
  </div>
</template>

<script>
import crudFundsDetail from '@/api/fundsDetail'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import MaterialList from "@/components/material";
import {getType} from '@/api/yxUserBill'
import { download } from '@/api/data'
import { downloadFile } from '@/utils/index'

// crud交由presenter持有
const defaultCrud = CRUD({ title: '平台资金明细', url: 'api/yxUserBillAll', sort: 'id,desc',optShow: {
      add: false,
      edit: false,
      del: false,
      download: false
    },
    query: {
      title:'',
      username:'',
      category:'',
      type:'',
      pm:'',
      addTimeStart:'',
      addTimeEnd:'',
      phone:'',
      linkId:'',
      userType:''
    }, crudMethod: { ...crudFundsDetail },
})
const defaultForm = { id: null, type: null, uid: null, username: null, linkId: null, pm: null, number: null, addTime: null }
export default {
  name: 'FundsDetail',
  components: { pagination, crudOperation, rrOperation, udOperation ,MaterialList},
  mixins: [presenter(defaultCrud), header(), form(defaultForm), crud()],
  data() {
    return {
      username: '', category: '', type: '',pm:'',
      addTimeStart:'',addTimeEnd:'',searchTime:'',
      permission: {
        add: ['admin', 'fundsDetail:add'],
        edit: ['admin', 'fundsDetail:edit'],
        del: ['admin', 'fundsDetail:del'],
        download: ['admin', 'yxUserRecharge:downloadAll']
      },
      rules: {
        type: [
          { required: true, message: '1:微商城下单,2:本地生活下单,3:微商城退款,4:本地生活退款不能为空', trigger: 'blur' }
        ],
        linkId: [
          { required: true, message: '订单号不能为空', trigger: 'blur' }
        ],
        pm: [
          { required: true, message: '明细种类; 0:支出;1:收入不能为空', trigger: 'blur' }
        ],
        number: [
          { required: true, message: '订单金额不能为空', trigger: 'blur' }
        ],
        addTime: [
          { required: true, message: '订单日期不能为空', trigger: 'blur' }
        ]
      },
      categoryOptions: [
        { value: '1', label: '微商城下单' },
        { value: '2', label: '本地生活下单' },
        { value: '3', label: '微商城退款' },
        { value: '4', label: '本地生活退款' }
      ],
      typeOptions: [],
      pmOptions: [
        { value: '0', label: '支出 ' },
        { value: '1', label: '收入' }
      ],
      userTypes: [
        { value: 4, label: '平台 ' },
        { value: 1, label: '商户' },
        { value: 2, label: '合伙人' },
        { value: 3, label: '用户' }
      ],
    }
  },
  created() {
    this.crud.resetQuery(false)
    this.$nextTick(() => {
      getType().then(res=>{
        if(res){
          this.typeOptions=res
        }
      }).catch(err=>{
        Message({ message: err, type: 'error' })
      })
    })
  },
  watch: {
  },
  computed:{
    typeLabel:function(){
      return function(type){
      if(this.typeOptions.length){
        let i= this.typeOptions.filter(function(item){
          for(let key in item){
            if(key===type){
              return JSON.parse(JSON.stringify(item))
            }
          }
        })
        if(i.length){
          return i[0][type]
        }
      }else{
        return ""
      }
    }
    },
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
    // 获取数据前设置好接口地址
    [CRUD.HOOK.beforeRefresh]() {
      if(this.crud.query.phone.length>0 && this.crud.query.userType===''){
        this.crud.notify('请选择用户类型','error')
        return false
      }
      return true
    }, // 新增与编辑前做的操作
    [CRUD.HOOK.afterToCU](crud, form) {
    },
    doExport() {
      crud.downloadLoading = true
      download('api/downloadUserBillAll', this.crud.getQueryParams()).then(result => {
        downloadFile(result, crud.title + '数据', 'xlsx')
        crud.downloadLoading = false
      }).catch(() => {
        crud.downloadLoading = false
      })
    },
  }
}



</script>

<style scoped>
  .table-img {
    display: inline-block;
    text-align: center;
    background: #ccc;
    color: #fff;
    white-space: nowrap;
    position: relative;
    overflow: hidden;
    vertical-align: middle;
    width: 32px;
    height: 32px;
    line-height: 32px;
  }
</style>
