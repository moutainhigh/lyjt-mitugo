<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!-- 搜索 -->
      <el-input v-model="query.value" clearable placeholder="输入搜索内容" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
      <el-select v-model="query.type" clearable placeholder="类型" class="filter-item" style="width: 130px">
        <el-option v-for="item in queryTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
      </el-select>
      <el-select v-model="query.isBest" clearable placeholder="精品推荐"
                 style="width: 200px;" class="filter-item">
        <el-option
          v-for="item in pstatusList"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-select v-model="query.isHot" clearable placeholder="热销榜单"
                 style="width: 200px;" class="filter-item">
        <el-option
          v-for="item in pstatusList"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
<!--      <el-select v-model="query.isShow" clearable placeholder="状态"-->
<!--                 style="width: 200px;" class="filter-item">-->
<!--        <el-option-->
<!--          v-for="item in statusList"-->
<!--          :key="item.value"-->
<!--          :label="item.label"-->
<!--          :value="item.value">-->
<!--        </el-option>-->
<!--      </el-select>-->
      <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="toQuery">搜索</el-button>
      <!-- 新增 -->
      <el-button
        type="danger"
        class="filter-item"
        size="mini"
        icon="el-icon-refresh"
        @click="toQuery"
      >刷新</el-button>
    </div>
    <!--表单组件-->
    <h5Form ref="h5" />
    <eForm ref="form" :is-add="isAdd" />
    <commission ref="form6"/>
    <!--表格渲染-->
    <el-table v-loading="loading" :data="data" size="small" style="width: 100%;">
      <el-table-column prop="id" label="商品id" />
      <el-table-column ref="table" prop="image" label="商品图片">
        <template slot-scope="scope">
          <a :href="scope.row.image" style="color: #42b983" target="_blank"><img :src="scope.row.image" alt="点击打开" class="el-avatar"></a>
        </template>
      </el-table-column>
      <el-table-column prop="store.storeName" label="商铺名称" />
      <el-table-column prop="storeName" label="商品名称" />
      <el-table-column prop="storeCategory.cateName" label="分类名称" />
      <el-table-column prop="price" label="商品价格" />
      <el-table-column prop="sales" label="销量" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <div @click="onSale(scope.row.id,scope.row.isShow)">
            <el-tag v-if="scope.row.isShow === 1" style="cursor: pointer" :type="''">已上架</el-tag>
            <el-tag v-else style="cursor: pointer" :type=" 'info' ">已下架</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="checkPermission(['admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_EDIT','YXSTOREPRODUCT_DELETE'])" label="操作" width="150px" align="center">
        <template slot-scope="scope">
          <el-button v-permission="['admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_EDIT']" size="mini" type="primary" icon="el-icon-edit" @click="edit(scope.row)" />
          <el-popover
            :ref="scope.row.id"
            v-permission="['admin','YXSTOREPRODUCT_ALL','YXSTOREPRODUCT_DELETE']"
            placement="top"
            width="180"
          >
            <p>确定删除本条数据吗？</p>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="$refs[scope.row.id].doClose()">取消</el-button>
              <el-button :loading="delLoading" type="primary" size="mini" @click="subDelete(scope.row.id)">确定</el-button>
            </div>
            <el-button slot="reference" type="danger" icon="el-icon-delete" size="mini" />
          </el-popover>
          <!-- <el-button v-permission="['admin','YXSTOREPRODUCT_EDIT']" slot="reference" type="info" plain size="mini" @click="h5(scope.row)">预览</el-button>
          <el-button v-permission="['admin', 'YXSTOREPRODUCT_RATE']" plain type="primary"  @click="commission(scope.row)" style="margin-top:5px">分佣配置</el-button> -->
            <el-dropdown trigger="click" style="margin-top:5px;" placement="bottom">
             <el-button type="primary" plain size="small" style="padding:9px 24px ">
              操作<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
                <el-dropdown-menu slot="dropdown">          
                    <el-dropdown-item >     
                      <el-button v-permission="['admin','YXSTOREPRODUCT_EDIT']" slot="reference"  size="mini" type="success" style="margin-top:5px;width:100%" @click="h5(scope.row)">预览</el-button>
                    </el-dropdown-item >
                    <el-dropdown-item >
                      <el-button v-permission="['admin', 'YXSTOREPRODUCT_RATE']" type="primary" @click="commission(scope.row)" style="margin-top:5px">分佣配置</el-button>
                    </el-dropdown-item>
                  
                </el-dropdown-menu>
             </el-dropdown>
       
       
       
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
import { del, onsale } from '@/api/yxStoreProduct'
import eForm from './form'
import h5Form from './h5'
import commission from './commission'
export default {
  components: { eForm, commission,h5Form },
  mixins: [initData],
  data() {
    return {
      delLoading: false,
      visible: false,
      queryTypeOptions: [
        { key: 'storeName', display_name: '商品名称' },
        { key: 'merUsername', display_name: '商户用户名' }
      ],
      statusList:[
        {value:0,label:'已下架'},
        {value:1,label:'已上架'}
      ],
      pstatusList:[
        {value:1,label:'是'},
        {value:0,label:'否'}
      ]
    }
  },
  created() {
    this.$nextTick(() => {
      this.init()
    })
  },
  methods: {
    checkPermission,
    beforeInit() {
      this.url = 'api/yxStoreProduct'
      const sort = 'id,desc'
      this.params = { page: this.page, size: this.size, sort: sort, isShow: 0, isDel: 0 }
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
    onSale(id, status) {
      this.$confirm(`确定进行[${status ? '下架' : '上架'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          onsale(id, { status: status }).then(({ data }) => {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1000,
              onClose: () => {
                this.init()
              }
            })
          })
        })
        .catch(() => { })
    },
    add() {
      this.isAdd = true
      this.$refs.form.dialog = true
      this.$refs.form.getCates()
    },
    edit(data) {
      this.isAdd = false
      const _this = this.$refs.form
      _this.getCates()
      _this.form = {
        id: data.id,
        merId: data.merId,
        image: data.image,
        sliderImage: data.sliderImage,
        imageArr: data.image.split(','),
        sliderImageArr: data.sliderImage.split(','),
        storeName: data.storeName,
        storeInfo: data.storeInfo,
        keyword: data.keyword,
        barCode: data.barCode,
        storeCategory: data.storeCategory || {id:null},
        storeCategoryId: data.cateFlg?data.storeCategory.id:null,
        price: data.price,
        vipPrice: data.vipPrice,
        otPrice: data.otPrice,
        postage: data.postage,
        unitName: data.unitName,
        sort: data.sort,
        sales: data.sales,
        stock: data.stock,
        isShow: data.isShow,
        isHot: data.isHot,
        isBenefit: data.isBenefit,
        isBest: data.isBest,
        isNew: data.isNew,
        description: data.description,
        addTime: data.addTime,
        isPostage: data.isPostage,
        isDel: data.isDel,
        merUse: data.merUse,
        giveIntegral: data.giveIntegral,
        cost: data.cost,
        isSeckill: data.isSeckill,
        isGood: data.isGood,
        ficti: data.ficti,
        browse: data.browse,
        codePath: data.codePath,
        soureLink: data.soureLink,
        commission: data.commission,
        settlement: data.settlement
      }
      _this.dialog = true
    },
    h5(data) {
      this.$refs.h5.dialog = true
      this.$refs.h5.id=data.id
    },
    commission(data) {
      const _this = this.$refs.form6
      _this.form = {
        id: data.id,
        customizeType:data.customizeType,
        yxCustomizeRate:data.yxCustomizeRate?data.yxCustomizeRate:{}
      }
      if(data.customizeType===2){
        Object.assign(_this.form2,data.yxCustomizeRate)
      }
      console.log('**********')
      console.log(_this.form)
      _this.dialog = true
    }
  }
}
</script>

<style scoped>

</style>
