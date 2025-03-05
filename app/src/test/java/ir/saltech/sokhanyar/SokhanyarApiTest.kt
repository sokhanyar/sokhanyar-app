package ir.saltech.sokhanyar

import ir.saltech.sokhanyar.api.config.ApiCallback
import ir.saltech.sokhanyar.api.config.ApiClient
import ir.saltech.sokhanyar.api.config.call
import ir.saltech.sokhanyar.api.response.ClinicsInfoResponse
import ir.saltech.sokhanyar.api.response.ErrorResponse
import org.junit.Test
import kotlin.Throwable

class SokhanyarApiTest {

	@Test
	fun `can get clinics info`() {
		ApiClient.sokhanyar.getClinicsInfo().call(object: ApiCallback<ClinicsInfoResponse> {
			override fun onSuccessful(responseObject: ClinicsInfoResponse?) {
				println(responseObject)
			}

			override fun onFailure(
				response: ErrorResponse?,
				t: Throwable?,
			) {
				if (t != null)
					throw t
				if (response != null)
					error(response.detail)
			}

		})
	}
}