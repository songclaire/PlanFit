<template>
    <div class="card">
        <h3 class="card-title">사용자 목록</h3>
        <TableArea v-model="memberTable">
            <template #cell-adminRole="{ row }">
                <q-toggle
                    v-model="row.isAdmin"
                    checked-icon="check"
                    unchecked-icon="clear"
                    color="primary"
                    :disable="row.userId === 'jieun92' || row._loading"
                    @update:model-value="val => toggleAdmin(row, val)"
                />
            </template>
        </TableArea>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useQuasar } from 'quasar'
import TableArea from '../../components/TableArea.vue'
import axios from 'axios'

const $q = useQuasar()

/**
 * 사용자 목록 (TableArea 사용)
**/
const memberTable = ref({
    url: '/api/user/memberList',
    columns: [
        {
            name: 'userId',
            label: '사용자아이디',
            align: 'center'
        },
        {
            name: 'userName',
            label: '사용자명',
            align: 'center'
        },
        {
            name: 'birthDate',
            label: '생년월일',
            align: 'center'
        },
        {
            name: 'telNo',
            label: '전화번호',
            align: 'center'
        },
        {
            name: 'adminRole',
            label: '관리자 권한',
            align: 'center'
        },
    ],
    items: [],
    options: {},
    request: {
        method: 'post',
    },
    errors: {},
    events: {},
})

/**
 * 토글 핸들러
**/
const toggleAdmin = async (row, val) => {
    const prev = !val
    row._loading = true
    try {
        await axios.post('/api/user/adminRole', {
            userId: row.userId,
            isAdmin: val
        })
        $q.notify({
            message: `${row.userId}의 권한이 변경되었습니다.`,
            color: 'primary',
            position: 'bottom',
            timeout: 2000
        })
    } catch (err) {
        row.isAdmin = prev
        $q.notify({
            message: '권한 변경에 실패했습니다.',
            color: 'negative',
            position: 'top-right'
        })
    } finally {
        row._loading = false
    }
}


</script>


<style scoped>
.card {
    background: #ffffff;
    border: 1px solid #e6ebf5;
    border-radius: 14px;
    padding: 18px;
}
.card-title {
    font-size: 18px;
    font-weight: 700;
    margin: 0 0 14px 0;
    color: #0b2447;
    padding-left: 50px;
    padding-top: 15px;
}
.placeholder-box {
    height: 280px;
    border: 2px dashed #cbd5e1;
    border-radius: 12px;
    display: grid;
    place-items: center;
    color: #64748b;
    font-weight: 600;
}
.card {
  background: #ffffff;
  border: 1px solid #e6ebf5;
  border-radius: 14px;
  padding: 18px;
}
.card-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 14px 0;
  color: #0b2447;
}
</style>
