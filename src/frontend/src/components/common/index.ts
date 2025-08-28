import type { App } from 'vue'
import ImageWithFallback from './ImageWithFallback.vue'
import CountUp from './CountUp.vue'
import CommonDialog from './CommonDialog.vue'
import DataTable from './DataTable.vue'
import DataCard from './DataCard.vue'
import StatusTag from './StatusTag.vue'
import FormValidator from './FormValidator.vue'
import FileUpload from './FileUpload.vue'
import ChartContainer from './ChartContainer.vue'
import CharityStatsCard from './CharityStatsCard.vue'
import SearchFilter from './SearchFilter.vue'

export default {
  install(app: App) {
    app.component('ImageWithFallback', ImageWithFallback)
    app.component('CountUp', CountUp)
    app.component('CommonDialog', CommonDialog)
    app.component('DataTable', DataTable)
    app.component('DataCard', DataCard)
    app.component('StatusTag', StatusTag)
    app.component('FormValidator', FormValidator)
    app.component('FileUpload', FileUpload)
    app.component('ChartContainer', ChartContainer)
    app.component('CharityStatsCard', CharityStatsCard)
    app.component('SearchFilter', SearchFilter)
  }
}

export { 
  ImageWithFallback, 
  CountUp, 
  CommonDialog, 
  DataTable, 
  DataCard, 
  StatusTag, 
  FormValidator, 
  FileUpload, 
  ChartContainer, 
  CharityStatsCard, 
  SearchFilter 
}

// 导出类型定义
export type { TableColumn, FilterConfig, StatusConfig } from './DataTable.vue'