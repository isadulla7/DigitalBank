package uz.fido.universaldigital.widgets.currency_rates.retrofit

interface Repository {
    fun getData(callback: (RepoResult) -> Unit)
}