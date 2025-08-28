/**
 * 数据导出工具函数
 * 支持Excel、CSV、JSON等格式的数据导出
 */

// 导出格式类型
export type ExportFormat = 'excel' | 'csv' | 'json' | 'txt'

// 导出配置接口
export interface ExportConfig {
  filename?: string
  sheetName?: string
  headers?: string[]
  fields?: string[]
  formatter?: (value: any, field: string, row: any) => string
}

/**
 * 导出为CSV格式
 */
export function exportToCSV(data: any[], config: ExportConfig = {}): void {
  const {
    filename = 'export.csv',
    headers,
    fields,
    formatter
  } = config

  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  // 获取字段列表
  const fieldList = fields || Object.keys(data[0])
  
  // 获取表头
  const headerList = headers || fieldList

  // 构建CSV内容
  const csvContent = [
    // 表头行
    headerList.map(header => `"${header}"`).join(','),
    // 数据行
    ...data.map(row => 
      fieldList.map(field => {
        let value = getNestedValue(row, field)
        
        // 应用格式化函数
        if (formatter) {
          value = formatter(value, field, row)
        }
        
        // 处理特殊字符
        if (typeof value === 'string') {
          value = value.replace(/"/g, '""') // 转义双引号
        }
        
        return `"${value || ''}"`
      }).join(',')
    )
  ].join('\n')

  // 添加BOM以支持中文
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8' })
  
  downloadFile(blob, filename)
}

/**
 * 导出为JSON格式
 */
export function exportToJSON(data: any[], config: ExportConfig = {}): void {
  const {
    filename = 'export.json',
    fields,
    formatter
  } = config

  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  let exportData = data

  // 如果指定了字段，只导出指定字段
  if (fields) {
    exportData = data.map(row => {
      const newRow: any = {}
      fields.forEach(field => {
        let value = getNestedValue(row, field)
        
        // 应用格式化函数
        if (formatter) {
          value = formatter(value, field, row)
        }
        
        newRow[field] = value
      })
      return newRow
    })
  }

  const jsonContent = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8' })
  
  downloadFile(blob, filename)
}

/**
 * 导出为Excel格式（简化版，使用HTML表格）
 */
export function exportToExcel(data: any[], config: ExportConfig = {}): void {
  const {
    filename = 'export.xlsx',
    sheetName = 'Sheet1',
    headers,
    fields,
    formatter
  } = config

  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  // 获取字段列表
  const fieldList = fields || Object.keys(data[0])
  
  // 获取表头
  const headerList = headers || fieldList

  // 构建HTML表格
  const tableHTML = `
    <table border="1">
      <thead>
        <tr>
          ${headerList.map(header => `<th>${header}</th>`).join('')}
        </tr>
      </thead>
      <tbody>
        ${data.map(row => `
          <tr>
            ${fieldList.map(field => {
              let value = getNestedValue(row, field)
              
              // 应用格式化函数
              if (formatter) {
                value = formatter(value, field, row)
              }
              
              return `<td>${value || ''}</td>`
            }).join('')}
          </tr>
        `).join('')}
      </tbody>
    </table>
  `

  // 创建Excel文件内容
  const excelContent = `
    <html xmlns:o="urn:schemas-microsoft-com:office:office" 
          xmlns:x="urn:schemas-microsoft-com:office:excel" 
          xmlns="http://www.w3.org/TR/REC-html40">
      <head>
        <meta charset="utf-8">
        <meta name="ProgId" content="Excel.Sheet">
        <meta name="Generator" content="Microsoft Excel 11">
        <style>
          table { border-collapse: collapse; }
          th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
          th { background-color: #f5f5f5; font-weight: bold; }
        </style>
      </head>
      <body>
        <div id="${sheetName}">
          ${tableHTML}
        </div>
      </body>
    </html>
  `

  const blob = new Blob([excelContent], { 
    type: 'application/vnd.ms-excel;charset=utf-8' 
  })
  
  downloadFile(blob, filename)
}

/**
 * 导出为文本格式
 */
export function exportToTXT(data: any[], config: ExportConfig = {}): void {
  const {
    filename = 'export.txt',
    headers,
    fields,
    formatter
  } = config

  if (!data || data.length === 0) {
    console.warn('没有数据可导出')
    return
  }

  // 获取字段列表
  const fieldList = fields || Object.keys(data[0])
  
  // 获取表头
  const headerList = headers || fieldList

  // 计算列宽
  const columnWidths = headerList.map((header, index) => {
    const field = fieldList[index]
    const maxContentLength = Math.max(
      header.length,
      ...data.map(row => {
        let value = getNestedValue(row, field)
        if (formatter) {
          value = formatter(value, field, row)
        }
        return String(value || '').length
      })
    )
    return Math.min(maxContentLength + 2, 30) // 最大宽度30
  })

  // 构建文本内容
  const lines = [
    // 表头行
    headerList.map((header, index) => 
      header.padEnd(columnWidths[index])
    ).join(' | '),
    
    // 分隔行
    columnWidths.map(width => '-'.repeat(width)).join('-+-'),
    
    // 数据行
    ...data.map(row => 
      fieldList.map((field, index) => {
        let value = getNestedValue(row, field)
        
        // 应用格式化函数
        if (formatter) {
          value = formatter(value, field, row)
        }
        
        return String(value || '').padEnd(columnWidths[index])
      }).join(' | ')
    )
  ]

  const txtContent = lines.join('\n')
  const blob = new Blob([txtContent], { type: 'text/plain;charset=utf-8' })
  
  downloadFile(blob, filename)
}

/**
 * 通用导出函数
 */
export function exportData(
  data: any[], 
  format: ExportFormat, 
  config: ExportConfig = {}
): void {
  switch (format) {
    case 'csv':
      exportToCSV(data, config)
      break
    case 'json':
      exportToJSON(data, config)
      break
    case 'excel':
      exportToExcel(data, config)
      break
    case 'txt':
      exportToTXT(data, config)
      break
    default:
      console.error(`不支持的导出格式: ${format}`)
  }
}

/**
 * 批量导出多个数据集
 */
export function exportMultipleDatasets(
  datasets: Array<{
    name: string
    data: any[]
    config?: ExportConfig
  }>,
  format: ExportFormat = 'json'
): void {
  if (format === 'json') {
    // JSON格式可以包含多个数据集
    const exportData = datasets.reduce((acc, dataset) => {
      acc[dataset.name] = dataset.data
      return acc
    }, {} as Record<string, any[]>)

    const jsonContent = JSON.stringify(exportData, null, 2)
    const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8' })
    downloadFile(blob, 'multiple-datasets.json')
  } else {
    // 其他格式分别导出
    datasets.forEach(dataset => {
      const config = {
        filename: `${dataset.name}.${format}`,
        ...dataset.config
      }
      exportData(dataset.data, format, config)
    })
  }
}

/**
 * 导出表格数据（专门为EnhancedDataTable设计）
 */
export function exportTableData(
  data: any[],
  columns: Array<{ key: string; label: string; formatter?: Function }>,
  format: ExportFormat,
  filename?: string
): void {
  const config: ExportConfig = {
    filename: filename || `table-export.${format}`,
    headers: columns.map(col => col.label),
    fields: columns.map(col => col.key),
    formatter: (value, field, row) => {
      const column = columns.find(col => col.key === field)
      if (column?.formatter) {
        return column.formatter(value, row, 0)
      }
      return value
    }
  }

  exportData(data, format, config)
}

/**
 * 获取嵌套对象的值
 */
function getNestedValue(obj: any, path: string): any {
  return path.split('.').reduce((current, key) => current?.[key], obj)
}

/**
 * 下载文件
 */
function downloadFile(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  
  link.href = url
  link.download = filename
  link.style.display = 'none'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  // 清理URL对象
  setTimeout(() => URL.revokeObjectURL(url), 100)
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 验证导出数据
 */
export function validateExportData(data: any[]): {
  isValid: boolean
  errors: string[]
  warnings: string[]
} {
  const errors: string[] = []
  const warnings: string[] = []

  if (!Array.isArray(data)) {
    errors.push('数据必须是数组格式')
    return { isValid: false, errors, warnings }
  }

  if (data.length === 0) {
    warnings.push('数据为空')
  }

  if (data.length > 10000) {
    warnings.push('数据量较大，导出可能需要较长时间')
  }

  // 检查数据一致性
  if (data.length > 0) {
    const firstRowKeys = Object.keys(data[0])
    const inconsistentRows = data.findIndex((row, index) => {
      if (index === 0) return false
      const currentKeys = Object.keys(row)
      return firstRowKeys.length !== currentKeys.length ||
             !firstRowKeys.every(key => currentKeys.includes(key))
    })

    if (inconsistentRows !== -1) {
      warnings.push(`第${inconsistentRows + 1}行数据结构与第一行不一致`)
    }
  }

  return {
    isValid: errors.length === 0,
    errors,
    warnings
  }
}

/**
 * 预览导出数据
 */
export function previewExportData(
  data: any[], 
  config: ExportConfig = {},
  maxRows: number = 5
): string {
  if (!data || data.length === 0) {
    return '没有数据可预览'
  }

  const previewData = data.slice(0, maxRows)
  const { fields, headers, formatter } = config
  
  const fieldList = fields || Object.keys(data[0])
  const headerList = headers || fieldList

  const preview = [
    '预览数据 (前' + Math.min(maxRows, data.length) + '行):',
    '',
    headerList.join(' | '),
    '-'.repeat(headerList.join(' | ').length),
    ...previewData.map(row => 
      fieldList.map(field => {
        let value = getNestedValue(row, field)
        if (formatter) {
          value = formatter(value, field, row)
        }
        return String(value || '')
      }).join(' | ')
    )
  ]

  if (data.length > maxRows) {
    preview.push(`... 还有 ${data.length - maxRows} 行数据`)
  }

  return preview.join('\n')
}