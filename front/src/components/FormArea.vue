<!-- FormArea.vue -->
<template>
    <div class="form-area" v-show="config.show">
        <!-- 폼 제목 -->
        <div class="form-header" v-if="config.title">
            <h3>{{ config.title }}</h3>
        </div>

        <!-- 폼 필드들 -->
        <form @submit.prevent="handleSubmit" @reset="handleReset">
            <div class="form-fields">
                <div
                    v-for="field in config.fields"
                    :key="field.name"
                    class="form-field"
                    :class="{ 'field-error': (field.errors?.length || 0) > 0 }"
                    v-show="field.show !== false"
                >
                    <!-- 라벨 -->
                    <label :for="field.name" class="field-label">
                        {{ field.label }}
                        <span v-if="field.required" class="required">*</span>
                    </label>

                    <!-- 입력 필드 -->
                    <div class="field-input">
                        <!-- 텍스트 입력 -->
                        <input
                            v-if="field.type === 'text' || field.type === 'email'"
                            :type="field.type"
                            :id="field.name"
                            v-model="field.value"
                            :disabled="field.disabled"
                            :readonly="field.readonly"
                            :placeholder="field.placeholder"
                            class="input-field"
                        />

                        <!-- 숫자 입력 -->
                        <input
                            v-else-if="field.type === 'number'"
                            type="number"
                            :id="field.name"
                            v-model.number="field.value"
                            :disabled="field.disabled"
                            :readonly="field.readonly"
                            :placeholder="field.placeholder"
                            class="input-field"
                        />

                        <!-- 날짜 입력 -->
                        <input
                            v-else-if="field.type === 'date'"
                            type="date"
                            :id="field.name"
                            v-model="field.value"
                            :disabled="field.disabled"
                            :readonly="field.readonly"
                            class="input-field"
                        />

                        <!-- 텍스트 -->
                        <textarea
                            v-else-if="field.type === 'textarea'"
                            :id="field.name"
                            v-model="field.value"
                            :disabled="field.disabled"
                            :readonly="field.readonly"
                            :placeholder="field.placeholder"
                            :rows="field.rows || 3"
                            class="input-field textarea-field"
                        ></textarea>

                        <!-- 셀렉트 -->
                        <select
                            v-else-if="field.type === 'select'"
                            :id="field.name"
                            v-model="field.value"
                            :disabled="field.disabled"
                            class="input-field select-field"
                        >
                            <option value="">선택하세요</option>
                            <option
                                v-for="option in field.options"
                                :key="option.value"
                                :value="option.value"
                            >
                                {{ option.label }}
                            </option>
                        </select>

                        <!-- 별점 입력 (여기에 추가) -->
                        <div v-else-if="field.type === 'star-rating'" class="star-rating">
                            <span
                                v-for="star in (field.max || 5)"
                                :key="star"
                                @click="!(field.readonly || field.disabled) && (field.value = star)"
                                class="star"
                                :class="{
                                    active: star <= (field.value || 0),
                                    disabled: field.readonly || field.disabled
                                }"
                            >
                                {{ star <= (field.value || 0) ? '★' : '☆' }}
                            </span>
                        </div>

                        <!-- 체크박스 -->
                        <input
                            v-else-if="field.type === 'checkbox'"
                            type="checkbox"
                            :id="field.name"
                            v-model="field.value"
                            :disabled="field.disabled"
                            class="checkbox-field"
                        />

                        <!-- 파일 업로드 -->
                        <template v-else-if="field.type === 'file'">
                            <!-- 보기모드: 이미지 프리뷰만 -->
                            <div v-if="(field.readonly || field.disabled) && field.value" class="file-only-preview-box">
                                <img
                                    class="file-only-preview"
                                    :src="(typeof field.value === 'string' && field.value.startsWith('http'))
                                            ? field.value
                                            : `/api/file/download/${field.value}`"
                                    alt="preview"
                                    @error="e => e.target.style.display = 'none'"
                                />
                            </div>

                            <!-- 편집모드: 업로더 -->
                            <AttachFile
                                v-else
                                v-model="field.value"
                                :accept="field.accept || 'image/*'"
                                :max-size-m-b="field.maxSizeMB || 5"
                            />
                        </template>



                        <!-- 필드 버튼들 -->
                        <div v-if="field.buttons" class="field-buttons">
                            <button
                                v-for="buttonName in field.buttons"
                                :key="buttonName"
                                type="button"
                                @click="handleFieldButtonClick(field.name, buttonName)"
                                class="field-button"
                            >
                                {{ getButtonLabel(buttonName) }}
                            </button>
                        </div>
                    </div>

                    <!-- 에러 메시지 -->
                    <div v-if="field.errors.length > 0" class="field-errors">
                        <span v-for="error in field.errors" :key="error" class="error-message">
                            {{ error }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- 폼 에러 메시지 -->
            <div v-if="config.errors && Object.keys(config.errors).length > 0" class="form-errors">
                <ul>
                    <li v-for="(errors, field) in config.errors" :key="field">
                        <strong>{{ getFieldLabel(field) }}:</strong>
                        <span v-for="error in errors" :key="error">{{ error }}</span>
                    </li>
                </ul>
            </div>

            <!-- 제출 버튼들 -->
            <div class="form-buttons" v-if="config.submitButtons !== false">
                <button type="submit" class="btn btn-primary" :disabled="config.loading">
                    {{ config.loading ? '처리중...' : '저장' }}
                </button>
                <button type="reset" class="btn btn-secondary" :disabled="config.loading">
                    초기화
                </button>
            </div>

            <!-- 커스텀 버튼들 -->
            <div class="custom-buttons" v-if="config.buttons && config.buttons.length > 0">
                <button
                    v-for="buttonName in config.buttons"
                    :key="buttonName"
                    type="button"
                    @click="handleCustomButtonClick(buttonName)"
                    :class="['btn', `btn-${buttonName}`]"
                >
                    {{ getButtonLabel(buttonName) }}
                </button>
            </div>
        </form>

        <!-- 로딩 오버레이 -->
        <div v-if="config.loading" class="loading-overlay">
            <div class="loading-spinner">처리중...</div>
        </div>
    </div>
</template>

<script>
import AttachFile from './AttachFile.vue'
export default {
    name: 'FormArea',
    components: {
        AttachFile  // 컴포넌트 등록
    },
    props: {
        modelValue: {
            type: Object,
            required: true
        }
    },
    emits: ['update:modelValue', 'success'],
    setup(props, { emit }) {
        const config = props.modelValue

        const buildModel = () =>
            Object.fromEntries((config.fields || []).map(f => [f.name, f.value]))

        console.log('[FormArea] handleSubmit 진입, typeof config.submit =', typeof config.submit)

        const handleSubmit = async () => {
            try {
                const model = buildModel()
                const result = await config.submit?.call(config, model)
                console.log('[FormArea] config.submit 결과:', result)
                if (result) emit('success', result)
            } catch (e) {
                console.error('[FormArea] submit 중 에러:', e)
            }
        }

        const handleReset = () => {
            if (typeof config.reset === 'function') config.reset()
            emit('update:modelValue', config)
        }

        const handleFieldButtonClick = (fieldName, buttonName) => {
            const handler = config.events[fieldName]?.buttons?.[buttonName]?.click
            if (handler) {
                handler()
            }
        }

        const handleCustomButtonClick = (buttonName) => {
            const handler = config.events?.buttons?.[buttonName]?.click
            if (handler) {
                handler({
                    value: buildModel(),       // 현재 폼 값
                    submit: handleSubmit,      // 내부 submit 트리거
                    reset: handleReset,        // 내부 reset 트리거
                    config                     // 원본 config (옵션)
                })
            }
        }

        const getButtonLabel = (buttonName) => {
            const buttonLabels = {
                save: '저장',
                cancel: '취소',
                close: '닫기',
                search: '검색',
                add: '추가',
                delete: '삭제',
                edit: '수정',
                list: '목록',
            }
            return buttonLabels[buttonName] || buttonName
        }

        const getFieldLabel = (fieldName) => {
            const field = config.fields.find(f => f.name === fieldName)
            return field ? field.label : fieldName
        }

        return {
            config,
            handleSubmit,
            handleReset,
            handleFieldButtonClick,
            handleCustomButtonClick,
            getButtonLabel,
            getFieldLabel
        }
    }
}
</script>

<style scoped>
.form-area {
    position: relative;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
}

.form-header h3 {
    margin: 0 0 20px 0;
    padding-bottom: 10px;
    border-bottom: 2px solid #007bff;
    color: #333;
}

.form-fields {
    display: grid;
    gap: 16px;
}

.form-field {
    display: grid;
    grid-template-columns: 120px 1fr;
    gap: 10px;
    align-items: start;
}

.field-label {
    font-weight: 500;
    color: #333;
    padding-top: 8px;
}

.required {
    color: #dc3545;
}

.field-input {
    display: flex;
    gap: 8px;
    align-items: center;
}

.input-field {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}

.input-field:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.input-field:disabled {
    background-color: #f8f9fa;
    color: #6c757d;
}

.textarea-field {
    resize: vertical;
    min-height: 80px;
}

.checkbox-field {
    width: auto;
}

.field-buttons {
    display: flex;
    gap: 4px;
}

.field-button {
    padding: 6px 12px;
    background: #f8f9fa;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}

.field-button:hover {
    background: #e9ecef;
}

.field-error .input-field {
    border-color: #dc3545;
}

.field-errors {
    grid-column: 2;
    margin-top: 4px;
}

.error-message {
    display: block;
    color: #dc3545;
    font-size: 12px;
}

.form-errors {
    margin: 16px 0;
    padding: 12px;
    background: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    color: #721c24;
}

.form-buttons, .custom-buttons {
    margin-top: 20px;
    display: flex;
    gap: 8px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
}

.btn-primary {
    background: #007bff;
    color: white;
}

.btn-primary:hover:not(:disabled) {
    background: #0056b3;
}

.btn-secondary {
    background: #6c757d;
    color: white;
}

.btn-secondary:hover:not(:disabled) {
    background: #545b62;
}

/* 목록 */
.btn-edit {
    background: #64B5F6;        /* 밝은 파랑 */
    color: #fff;
    border: 1px solid #3B82F6;  /* 살짝 진한 테두리 */
    box-shadow: 0 2px 6px rgba(59, 130, 246, 0.25);
}
.btn-edit:hover:not(:disabled) {
    background: #3B82F6;        /* hover 시 살짝 진하게 */
    border-color: #3B82F6;
}

.btn-edit:focus-visible {
    outline: none;
    box-shadow: 0 0 0 3px rgba(100, 181, 246, 0.35);
}
.btn-edit:disabled {
    opacity: .6;
    cursor: not-allowed;
}
.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
}

.loading-spinner {
    padding: 20px;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.star-rating {
    display: flex;
    gap: 4px;
}

.star {
    cursor: pointer;
    font-size: 24px;
    color: #ddd;
    transition: color 0.2s;
}

.star.active {
    color: #ffd700;
}

.star:hover {
    color: #ffed4e;
}

.star.disabled {
    cursor: not-allowed;
    opacity: .6;
}

.file-only-preview-box {
    width: 100%;
}

.file-only-preview {
    display: block;
    width: 100%;
    height: auto;
    max-height: 350px;      /* 필요시 360~720px 등으로 조절 */
    object-fit: contain;    /* 비율 유지, 안 잘리게 */
    border: none;           /* 테두리 제거 */
    box-shadow: none;       /* 혹시 전역 그림자 있으면 제거 */
    border-radius: 6px;     /* 모서리 둥글게 유지 원치 않으면 0으로 */
}


</style>
