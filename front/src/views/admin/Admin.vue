<template>
    <div class="admin-wrapper">
        <div class="header-row">
            <h2>관리자 페이지</h2>
        </div>

        <!-- 상단 탭 -->
        <q-tabs class="admin-tabs" dense>
            <q-route-tab :to="{ name: 'AdminMember' }" label="사용자" />
            <!--<q-route-tab :to="{ name: 'AdminRole' }" label="권한" /> -->
        </q-tabs>

        <!-- 자식 라우트 렌더: 목록성 화면들은 캐싱 -->
        <router-view v-slot="{ Component, route: r }">
            <keep-alive :include="CACHE_NAMES">
                <component :is="Component" :key="r.fullPath" />
            </keep-alive>
        </router-view>
    </div>
</template>

<script setup>
import { ref } from 'vue'

// keep-alive 포함시킬 컴포넌트 이름들(자식 SFC의 name 옵션과 일치 필요)
const CACHE_NAMES = ref(['AdminMemberView', 'AdminRoleView', 'AdminAuditView'])
</script>

<style scoped>
.admin-wrapper {
    max-width: 1200px;
    margin: 40px auto;
    padding: 32px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}
.header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}
.header-row h2 {
    font-size: 28px;
    margin: 0;
    font-weight: 800;
    letter-spacing: -0.02em;
    color: #0f172a;
}

.admin-tabs {
    background-color: #ffffff;
    border-bottom: 1px solid #e0e6f1;
    padding-left: 8px;
    margin-bottom: 16px;
}
.admin-tabs .q-tab {
    padding: 5px 22px;
    font-size: 16px;
    font-weight: 500;
    color: #555;
    border-bottom: 2px solid transparent;
    transition: all 0.2s ease-in-out;
}
.admin-tabs .q-tab.q-tab--active {
    border-bottom: 2px solid #3B82F6;
    color: #3B82F6;
    background-color: #e8f0fe;
    font-weight: 600;
    border-radius: 8px 8px 0 0;
}
.admin-tabs .q-tab:hover {
    background-color: #dbeafe;
    color: #2563EB;
    border-radius: 8px 8px 0 0;
}
</style>
