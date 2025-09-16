<!-- AttachFile.vue -->
<template>
    <div class="attach-file">
        <!-- 파일이 있을 때 미리보기 -->
        <div v-if="showPreview && fileId && !uploading" class="file-preview">
            <img
                :src="fileUrl"
                :alt="fileName"
                class="preview-image"
                @error="handleImageError"
            />
            <div class="file-info">
                <span class="file-name">{{ fileName }}</span>
                <div class="file-actions">
                    <button
                        type="button"
                        @click="downloadFile"
                        class="btn-download"
                        title="다운로드"
                    >
                        📥
                    </button>
                    <button
                        type="button"
                        @click="removeFile"
                        class="btn-remove"
                        title="삭제"
                    >
                        ❌
                    </button>
                </div>
            </div>
        </div>

        <!-- 파일 선택 영역 -->
        <div v-else class="file-upload">
            <input
                ref="fileInput"
                type="file"
                :accept="accept"
                @change="handleFileSelect"
                class="file-input"
                :disabled="uploading"
            />

            <button
                v-if="ui === 'button'"
                type="button"
                class="btn btn-outline"
                :disabled="disabled || uploading"
                @click="triggerFileSelect"
            >
                {{ label }}<span v-if="uploading"> (업로드 중…)</span>
            </button>

            <!-- 수정: 드롭존 모드(버튼 모드일 땐 렌더 안 함) -->
            <div
                v-else
                class="upload-area"
                @click="triggerFileSelect"
                @dragover.prevent="handleDragOver"
                @drop.prevent="handleDrop"
            >
                <div v-if="uploading" class="uploading">
                    <div class="spinner"></div>
                    <span>업로드 중...</span>
                </div>
                <div v-else class="upload-prompt">
                    <div class="upload-icon">📎</div>
                    <div class="upload-text">
                        <div>파일을 선택하거나 여기에 드래그하세요</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="error" class="error-message">
            {{ error }}
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    accept: {
        type: String,
        default: 'image/*'
    },
    maxSizeMB: {
        type: Number,
        default: 10
    },
    menuType: {
        type: String,
        required: true
    },
    ui: {
        type: String,
        default: 'dropzone'
    },
    label: {
        type: String,
        default: '사진 추가'
    },
    disabled: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['update:modelValue', 'file-uploaded', 'file-removed'])

// 상태 관리
const fileInput = ref(null)
const fileId = ref(props.modelValue)
const fileName = ref('')
const uploading = ref(false)
const error = ref('')

// 업로드 후 이미지 미리보기를 위한 다운로드 url
const fileUrl = computed(() => {
    return fileId.value ? `/api/file/download/${fileId.value}` : null
})

const maxFileSize = computed(() => {
    return props.maxSizeMB * 1024 * 1024 // MB를 bytes로 변환
})

// modelValue 변경 감지
watch(() => props.modelValue, (newValue) => {
    fileId.value = newValue
})

// 파일 선택 트리거
const triggerFileSelect = () => {
    if (!uploading.value) fileInput.value?.click()
}

// 파일 선택 처리
const handleFileSelect = (event) => {
    const file = event.target.files[0]
    if (file) {
        uploadFile(file)
    }
}

// 파일 검증
const validateFile = (file) => {
    // 파일 크기 체크
    if (file.size > maxFileSize.value) {
        throw new Error(`파일 크기가 ${props.maxSizeMB}MB를 초과합니다.`)
    }

    // 파일 타입 체크 (이미지만 허용하는 경우)
    if (props.accept === 'image/*' && !file.type.startsWith('image/')) {
        throw new Error('이미지 파일만 업로드 가능합니다.')
    }

    return true
}

// 파일 업로드
const uploadFile = async (file) => {
    try {
        error.value = ''
        uploading.value = true
        validateFile(file)

        const formData = new FormData()
        formData.append('file', file)
        formData.append('menuType', props.menuType)

        // axios 사용
        const response = await axios.post('/api/file/upload', formData)

        // axios는 response.data로 접근
        fileId.value = response.data.fileId
        fileName.value = file.name

        emit('update:modelValue', fileId.value)
        emit('file-uploaded', {
            fileId: fileId.value,
            fileName: fileName.value,
            originalFile: file
        })

        if (fileInput.value) {
            fileInput.value.value = ''
        }

    } catch (err) {
        error.value = err.message
        console.error('파일 업로드 오류:', err)
    } finally {
        uploading.value = false
    }
}

// 파일 제거
const removeFile = () => {
    if (confirm('파일을 삭제하시겠습니까?')) {
        const removedFileId = fileId.value
        const removedFileName = fileName.value

        fileId.value = null
        fileName.value = ''
        error.value = ''

        emit('update:modelValue', null)
        emit('file-removed', {
            fileId: removedFileId,
            fileName: removedFileName
        })
    }
}

// 파일 다운로드
const downloadFile = () => {
    if (fileId.value) {
        const link = document.createElement('a')
        link.href = `/api/file/download/${fileId.value}`
        link.download = fileName.value || 'download'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
    }
}

// 이미지 로드 에러 처리
const handleImageError = () => {
    error.value = '이미지를 불러올 수 없습니다.'
}

// 드래그 앤 드롭 지원 (선택사항)
const handleDragOver = (event) => {
    event.preventDefault()
}

const handleDrop = (event) => {
    event.preventDefault()
    const files = event.dataTransfer.files
    if (files.length > 0) {
        uploadFile(files[0])
    }
}
</script>


<style scoped>
.attach-file {
    width: 100%;
    max-width: 400px;
}

.file-preview {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 12px;
    background: #f9f9f9;
}

.preview-image {
    width: 100%;
    max-height: 200px;
    object-fit: cover;
    border-radius: 4px;
    margin-bottom: 8px;
}

.file-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.file-name {
    font-size: 14px;
    color: #555;
    flex: 1;
    margin-right: 8px;
    word-break: break-all;
}

.file-actions {
    display: flex;
    gap: 4px;
}

.btn-download,
.btn-remove {
    background: none;
    border: none;
    padding: 4px 8px;
    cursor: pointer;
    border-radius: 4px;
    font-size: 14px;
}

.btn-download:hover {
    background: #e3f2fd;
}

.btn-remove:hover {
    background: #ffebee;
}

.file-upload {
    position: relative;
}

.file-input {
    display: none;
}

.upload-area {
    border: 2px dashed #ddd;
    border-radius: 8px;
    padding: 20px 20px;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
    background: #fafafa;
}

.upload-area:hover {
    border-color: #007bff;
    background: #f0f7ff;
}

.upload-area.uploading {
    cursor: not-allowed;
    opacity: 0.7;
}

.uploading {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
}

.spinner {
    width: 20px;
    height: 20px;
    border: 2px solid #f3f3f3;
    border-top: 2px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.upload-prompt {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 3px;
}

.upload-icon {
    font-size: 25px;
    color: #666;
}

.upload-text {
    color: #666;
    font-size: 12px;
}

.upload-text div:first-child {
    font-weight: 500;
    margin-bottom: 4px;
}

.error-message {
    margin-top: 8px;
    padding: 8px 12px;
    background: #ffebee;
    border: 1px solid #ffcdd2;
    border-radius: 4px;
    color: #c62828;
    font-size: 14px;
}
</style>