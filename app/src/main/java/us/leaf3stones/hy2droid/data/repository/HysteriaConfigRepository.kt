package us.leaf3stones.hy2droid.data.repository

import us.leaf3stones.hy2droid.data.datasource.HysteriaConfigDataSource
import us.leaf3stones.hy2droid.data.model.HysteriaConfig

class HysteriaConfigRepository(private val singleSaveConfigDataStore: HysteriaConfigDataSource = HysteriaConfigDataSource()) {
    suspend fun saveConfig(config: HysteriaConfig) {
        singleSaveConfigDataStore.saveConfig(config)
    }

    suspend fun loadConfig() =
        singleSaveConfigDataStore.loadConfig()

}