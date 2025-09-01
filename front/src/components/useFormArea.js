// composables/useFormArea.js
import { reactive, ref, computed } from 'vue'

// 간단한 useFormArea 컴포저블
export function useFormArea(config) {
    const formConfig = reactive({
        // 기본값들
        id: generateId(),
        name: config.name || '',
        title: config.title || '',
        model: config.model || {},
        fields: config.fields || [],
        events: config.events || {},
        errors: {},
        show: true,
        loading: false,
        submitButtons: config.submitButtons !== false, // 기본값 true

        // 사용자 설정으로 덮어쓰기
        ...config
    })

    // 필드 초기화
    initFields(formConfig)

    // 이벤트 처리
    processEvents(formConfig)

    // 폼 메서드들
    formConfig.submit = function() {
        if (!this.validate()) {
            return null
        }

        const formData = {}
        this.fields.forEach(field => {
            formData[field.name] = field.value
        })

        // 사용자 정의 SUBMIT 이벤트 실행
        if (this.events.SUBMIT) {
            this.events.SUBMIT.call(this, formData)
        }

        return formData
    }

    formConfig.setModel = function(data) {
        this.model = { ...data }
        this.errors = {}

        this.fields.forEach(field => {
            field.value = data[field.name] || ''
            field.errors = []
        })
    }

    formConfig.reset = function() {
        this.setModel(this.model)
        if (this.events.RESET) {
            this.events.RESET.call(this)
        }
    }

    formConfig.validate = function() {
        let isValid = true
        this.errors = {}

        this.fields.forEach(field => {
            field.errors = []

            // 필수값 체크
            if (field.required && (!field.value || field.value.toString().trim() === '')) {
                field.errors.push('필수 입력값입니다.')
                this.errors[field.name] = field.errors
                isValid = false
            }

            // 이메일 검증
            if (field.type === 'email' && field.value) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
                if (!emailRegex.test(field.value)) {
                    field.errors.push('올바른 이메일 형식이 아닙니다.')
                    this.errors[field.name] = field.errors
                    isValid = false
                }
            }
        })

        return isValid
    }

    formConfig.getField = function(fieldName) {
        return this.fields.find(f => f.name === fieldName)
    }

    return formConfig
}

// 필드 초기화
function initFields(config) {
    config.fields.forEach(field => {
        // 기본값 설정
        field.value = field.value || config.model[field.name] || ''
        field.errors = field.errors || []
        field.show = field.show !== false
        field.disabled = field.disabled || false
        field.readonly = field.readonly || false

        // 기본 타입 설정
        if (!field.type) {
            field.type = 'text'
        }
    })
}

// 이벤트 처리 (간단 버전)
function processEvents(config) {
    const processedEvents = {}

    Object.keys(config.events).forEach(key => {
        const handler = config.events[key]

        if (key.includes('.')) {
            // 중첩 이벤트 처리 (예: 'field.button.click')
            const parts = key.split('.')
            let current = processedEvents

            for (let i = 0; i < parts.length - 1; i++) {
                if (!current[parts[i]]) {
                    current[parts[i]] = {}
                }
                current = current[parts[i]]
            }

            current[parts[parts.length - 1]] = handler.bind(config)
        } else {
            // 일반 이벤트
            processedEvents[key] = handler.bind(config)
        }
    })

    config.events = processedEvents
}

// 랜덤 ID 생성
function generateId() {
    return 'form_' + Math.random().toString(36).substr(2, 9)
}