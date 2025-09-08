<template>
  <div class="table-area">
    <!-- 테이블 상단 영역 -->
    <div class="table-header" v-if="config.title">
      <h4>{{ config.title }}</h4>
    </div>

    <!-- 메인 테이블 -->
    <q-table
      v-bind="qTableConfig"
      :loading="config.loading"
      flat
      :no-data-label="'데이터가 없습니다'"
    >
      <!-- 테이블 헤더 -->
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th
            v-for="col in props.cols"
            :key="col.name"
            :props="props"
          >
            {{ col.label }}
          </q-th>
        </q-tr>
      </template>

      <!-- 테이블 바디 -->
      <template v-slot:body="props">
        <q-tr :props="props" @click="onRowClick($event, props.row, props.rowIndex)"> <!-- 추가 -->
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            <slot :name="'cell-' + col.name" :row="props.row" :col="col" :value="props.row[col.name]">
              <template v-if="typeof col.format === 'function'">
                <span v-html="col.format(props.row[col.name], props.row, col)"></span>
              </template>
              <template v-else>
                {{ props.row[col.name] }}
              </template>
            </slot>
          </q-td>
        </q-tr>
      </template>

      <!-- 로딩 상태 -->
      <template v-slot:loading>
        <q-inner-loading :showing="true">
          <q-spinner-gears size="50px" color="primary" />
        </q-inner-loading>
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import _ from 'lodash'
import axios from 'axios'

const route = useRoute()

const props = defineProps({
    modelValue: {
        type: Object,
        required: true,
    },
})

// emit
const config = ref(props.modelValue)

const defaultConfig = {
    title: null,
    gdcc: null,
    items: [],
    url: null,
    search: {},
    columns: [],
    options: {},
    selected: [],
    pageInfo: {},
    loading: false,
    events: {
        INIT(evt) {},
    },
    request: {
        method: 'get',
        headers: {},
        buildParams: (s) => s,
        buildBody: (s) => s,
    }
}

// config와 defaultConfig의 병합
for (const key of Object.keys(defaultConfig)) {
    if (!config.value[key]) {
        config.value[key] = defaultConfig[key]
    }
}

config.value.request = config.value.request || {}
for (const k of Object.keys(defaultConfig.request)) {
  if (typeof config.value.request[k] === 'undefined') {
    config.value.request[k] = defaultConfig.request[k]
  }
}

const qTableConfig = computed(() => ({
    rows: config.value.items,
    columns: config.value.columns,
    rowKey: config.value.options.rowKey || 'id'
}))

function onRowClick(evt, item, index) {
  config.value.events?.['row-click']?.({ evt, item, index, value: { item, index } })
}

function onRequest(props) {
}

async function getList() {
    console.log('config.value?.search??', config.value?.search)
    if (!config.value.url) return
    config.value.loading = true
    try {
        const token = localStorage.getItem('accessToken')

        // method/headers 결정 (기본은 get)
        const method = (config.value.request?.method || 'get').toLowerCase()
        const baseHeaders = { ...(config.value.request?.headers || {}) }
        if (token) baseHeaders.Authorization = `Bearer ${token}`

        // search 가공 훅 적용
        // GET/DELETE: 쿼리스트링으로 적합하게 변환(buildParams)
        // POST/PUT  : 바디로 적합하게 변환(buildBody)
        const search = config.value.search || {}
        const asParams = config.value.request?.buildParams?.(search) ?? search
        const asBody = config.value.request?.buildBody?.(search) ?? search

        // axios 설정을 메서드에 따라 분기 : 메서드에 따라 params/data 자동 배치
        const axiosCfg = { url: config.value.url, method, headers: baseHeaders }
        if (method === 'get' || method === 'delete' ) {
            axiosCfg.params = asParams
        } else {
            axiosCfg.data = asBody
        }
        // 호출
        const {data} = await axios(axiosCfg)
        // 응답 통합(서버가 무엇을 주든 items = 배열로 맞춤)
        config.value.items = Array.isArray(data)        ? data
                       : Array.isArray(data?.list)  ? data.list
                       : Array.isArray(data?.rows)  ? data.rows
                       : []
    } catch (e) {
        console.error('목록 로딩 실패')
        config.value.items = []
    } finally {
        config.value.loading = false
    }
}

onMounted(() => {
    console.log('<데이터 1> request 확인', config.value.request)
    // 부모가 직접 호출할 수 있게 config에 메서드 붙임
    config.value.getList = getList
    getList()
})
</script>

<style scoped>
/* 기존 스타일에서 이 부분들만 교체하세요 */

.table-area {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;  /* 가운데 정렬 */
  background: #ffffff;
  border-radius: 0px;
  box-shadow: 0;
  overflow: hidden;
  border: none;
}

.table-header {
  padding: 15px 5px 13px;
  border-bottom: 0px solid #ffffff;
  background: transparent;
}

.table-header h4 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.025em;
}

:deep(.q-table thead tr) {
  background: #E4E9F0;
}

:deep(.q-table thead th) {
  padding: 18px 16px;
  font-weight: 600;
  font-size: 0.99rem;
  color: #1D3557;  /* 텍스트는 진한 회색으로 */
  border-bottom: 2px solid #e2e8f0;
  text-align: center;
  letter-spacing: 0.025em;
}

:deep(.q-table tbody td) {
  padding: 16px;
  font-size: 0.98rem;
  color: #374151;
  border-bottom: 0px solid #f8fafc;
  transition: background-color 0.2s ease;
}

:deep(.q-table tbody tr:hover) {
  background-color: #F5F7FA;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 텍스트 정렬 */
:deep(.text-right) {
  text-align: right;
}

:deep(.text-center) {
  text-align: center;
}

/* 데이터 없음 메시지 */
:deep(.q-table__message) {
  padding: 40px 20px;
  color: #999;
  font-size: 0.95rem;
}

/* 로딩 스피너 */
:deep(.q-inner-loading) {
  background: rgba(255, 255, 255, 0.8);
}

/* 반응형 */
@media (max-width: 768px) {
  .table-header {
    padding: 16px 16px 12px;
  }

  .table-header h4 {
    font-size: 1.1rem;
  }

  :deep(.q-table thead th),
  :deep(.q-table tbody td) {
    padding: 12px 8px;
    font-size: 0.85rem;
  }
}

@media (max-width: 600px) {
  :deep(.q-table thead th),
  :deep(.q-table tbody td) {
    padding: 10px 6px;
    font-size: 0.8rem;
  }
}

:deep(.q-table tbody tr) { cursor: pointer; }
</style>