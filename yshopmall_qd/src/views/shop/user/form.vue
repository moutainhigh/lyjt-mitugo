<template>
  <el-dialog :append-to-body="true" :close-on-click-modal="false" :before-close="cancel" :visible.sync="dialog" :title="isAdd ? '新增' : '编辑'" width="550px">
    <el-form ref="form" :model="form" :rules="rules" size="small" label-width="120px">
      <el-form-item label="用户昵称">
        <el-input v-model="form.nickname" :disabled="true" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="真实姓名" prop='realName'>
        <el-input v-model="form.realName" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="用户备注" prop='mark'>
        <el-input v-model="form.mark" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="手机号码" prop='phone'>
        <el-input v-model="form.phone" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="分享达人">
        <el-radio v-model="form.userRole" :label="1">分享达人</el-radio>
        <el-radio v-model="form.userRole" :label="0">普通会员</el-radio>
      </el-form-item>
      <el-form-item label="银行卡号">
        <el-input v-model="form.bankNo" :disabled="true" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="联行号">
        <el-input v-model="form.cnapsCode" :disabled="true" style="width: 370px;" />
      </el-form-item>
      <el-form-item label="银行预留手机号">
        <el-input v-model="form.bankMobile" :disabled="true" style="width: 370px;" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="cancel">取消</el-button>
      <el-button :loading="loading" type="primary" @click="doSubmit">确认</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { add, edit } from '@/api/yxUser'
import { isvalidPhone } from '@/utils/validate'
export default {
  props: {
    isAdd: {
      type: Boolean,
      required: true
    }
  },
  data() {
    // 自定义验证
    const validPhone = (rule, value, callback) => {
      if(!value){
        callback()
      }else if (!isvalidPhone(value)) {
        callback(new Error('请输入正确的11位手机号码'))
      } else {
        callback()
      }
    }
    return {
      loading: false, dialog: false,
      form: {
        uid: '',
        account: '',
        pwd: '',
        realName: '',
        birthday: '',
        cardId: '',
        mark: '',
        partnerId: '',
        groupId: '',
        nickname: '',
        avatar: '',
        phone: '',
        addTime: '',
        addIp: '',
        lastTime: '',
        lastIp: '',
        nowMoney: '',
        brokeragePrice: '',
        integral: '',
        signNum: '',
        status: '',
        level: '',
        spreadUid: '',
        spreadTime: '',
        userType: '',
        isPromoter: 0,
        userRole: 0,
        payCount: '',
        spreadCount: '',
        cleanTime: '',
        addres: '',
        adminid: 0,
        loginType: ''
      },
      rules: {
        realName:[
          { max: 4, message: '长度不超过 4 个字符', trigger: 'blur' }
        ],
        mark:[
          { max: 200, message: '长度不超过 200 个字符', trigger: 'blur' }
        ],
        phone:[
          { validator: validPhone,trigger: 'blur', }
        ],
      }
    }
  },
  methods: {
    cancel() {
      this.resetForm()
    },
    doSubmit() {
      this.loading = true
      this.$refs.form.validate((ret,obj)=>{
        if(ret){
          if (this.isAdd) {
            this.doAdd()
          } else this.doEdit()
        }else{
          this.loading = false
        }
      })
    },
    doAdd() {
      add(this.form).then(res => {
        this.resetForm()
        this.$notify({
          title: '添加成功',
          type: 'success',
          duration: 2500
        })
        this.loading = false
        this.$parent.init()
      }).catch(err => {
        this.loading = false
        console.log(err.response.data.message)
      })
    },
    doEdit() {
      edit(this.form).then(res => {
        this.resetForm()
        this.$notify({
          title: '修改成功',
          type: 'success',
          duration: 2500
        })
        this.loading = false
        this.$parent.init()
      }).catch(err => {
        this.loading = false
        console.log(err.response.data.message)
      })
    },
    resetForm() {
      this.dialog = false
      this.$refs['form'].resetFields()
      this.form = {
        uid: '',
        account: '',
        pwd: '',
        realName: '',
        birthday: '',
        cardId: '',
        mark: '',
        partnerId: '',
        groupId: '',
        nickname: '',
        avatar: '',
        phone: '',
        addTime: '',
        addIp: '',
        lastTime: '',
        lastIp: '',
        nowMoney: '',
        brokeragePrice: '',
        integral: '',
        signNum: '',
        status: '',
        level: '',
        spreadUid: '',
        spreadTime: '',
        userType: '',
        isPromoter: '',
        userRole: '',
        payCount: '',
        spreadCount: '',
        cleanTime: '',
        addres: '',
        adminid: '',
        loginType: ''
      }
    }
  }
}
</script>

<style scoped>

</style>
