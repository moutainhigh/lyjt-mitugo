<template>
  <div class="app-container">
    <div class="head-container">
      <el-input v-model="query.blurry" clearable placeholder="请输入用户名" style="width: 200px;marginRight:20px;" class="filter-item" />        
      <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="toQuery">搜索</el-button>
      <el-button
        type="danger"
        class="filter-item"
        size="mini"
        icon="el-icon-refresh"
        @click="toQuery"
      >刷新</el-button>
    </div>
    <!--表格渲染-->
    <el-table v-loading="loading" :data="data" size="small" style="width: 100%;">
      <el-table-column prop="nickname" label="用户名" />
      <el-table-column prop="requestIp" label="IP" />
      <el-table-column prop="address" label="地址来源" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="productType" label="商品类型" >
        <template slot-scope="scope">
          <span>{{ productType[scope.row.productType] }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="productId" label="商品Id" />
      <el-table-column prop="createTime" label="创建日期" width="180px">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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
import initData from '@/mixins/crud'
import { parseTime } from '@/utils/index'
export default {
  name: 'Log',
  mixins: [initData],
  data(){
    return {
      productType:['','商品','卡券']
    }
  },
  created() {
    this.$nextTick(() => {
      this.init()
    })
  },
  methods: {
    parseTime,
    beforeInit() {
      this.url = 'api/logs/mlogs'
      const query = this.query
      const value = query.value
      this.params = { page: this.page, size: this.size }
      if (value) { this.params['blurry'] = value }
      return true
    }
  }
}
</script>

<style scoped>

</style>
