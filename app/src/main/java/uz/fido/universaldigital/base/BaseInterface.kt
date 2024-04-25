package uz.fido.universaldigital.base

import uz.fido.network.domain.model.amount_requests.RmList
import uz.fido.network.domain.model.applications.OrderCardApp
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.ProductType
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.limits.SvLimit
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.network.domain.model.mib.Mib
import uz.fido.network.domain.model.mib.MibDetail
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringItem
import uz.fido.network.domain.model.monitoring.filter.FilterCard
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.news.Notification
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.network.domain.model.template.Template
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import uz.fido.universaldigital.ui.fragments.profile.about_bank.branches.BranchesAdapter

interface BaseInterface {
    fun setToEditText(allServiceLists: AllServiceLists, tag: String) {}
    fun openMibInfo(mib: Mib, position: Int) {}
    fun openMibInfoLongClick(mib: Mib, position: Int) {}
    fun openInfoMib(mibDetail: MibDetail) {}
    fun openLocalPayment(localPayment: LocalPayment) {}
    fun selectedCardWithOperation(priceItem: ProductType, operation: String) {}

    fun selectedCardBg(imageName: String) {}
    fun selectedBranch(country: Branches) {}
    fun selectedCard(card: CardResponse) {}
    fun selectedWallet(item: CardResponse) {}
    fun shareCardNumberDialog(item: CardResponse) {}
    fun switchList() {}
    fun copyCardNumber() {}
    fun shareCardNumber() {}
    fun addTemplate() {}
    fun openTemplate(template: Template) {}
    fun setPaymentParams(position: Int) {}
    fun sendCalculatorRange(range: Long, from: String, to: String) {}
    fun openHomeOperation(id: Int) {}
    fun openBankProduct(id: Int) {}
    fun addToHiddenWidgets(position: Int) {}
    fun addToVisibleWidgets(position: Int) {}
    fun updateWidgetList(list: ArrayList<MainWidget>) {}

    fun monitoringFilterCard(filterCard: FilterCard) {}
    fun monitoringPayed(userPayedService: UserPayedService) {}
    fun repeatPayment(localeMonitoring: LocalMonitoring) {}
    fun returnPayment(localeMonitoring: LocalMonitoring) {}
    fun fullInfo(localeMonitoring: LocalMonitoring) {}
    fun uzCardInfo(svMonitoringItem: SVMonitoringItem) {}
    fun humoInfoPaymentMonitoring(item: HumoMonitoringItem) {}
    fun visaInfoPaymentMonitoring(item: CurrencyCardMonitoringItem) {}
    fun walletInfoPaymentMonitoring(item: AccountHistory) {}
    fun p2pOperation(id: Int) {}
    fun requestMoneyCopyUrl(item: RmList) {}
    fun requestMoneyShareUrl(item: RmList) {}
    fun requestMoneyActivate(item: RmList) {}
    fun requestMoneyPause(item: RmList) {}
    fun requestMoneyDelete(item: RmList) {}
    fun openNewPage(position: Int) {}
    fun openBranchDetails(branch: Branches, viewHolder: BranchesAdapter.ViewHolder) {}
    fun terminateSession(item: UserDevices) {}
    fun terminateSessionType(type: String) {}
    fun myHouseService(template: Template, position: Int) {}
    fun makeDragAndDropOperation(fromPosition: Int, toPosition: Int) {}
    fun setMfo(mfo: String) {}
    fun myHouseMoreIcon(myHouseGroup: MyHouseGroup, type: String) {}
    fun myHouseService(template: Template) {}
    fun listOperation(position: Int) {}
    fun openLimitItem(item: SvLimit) {}
    fun getApplicationDetails(item: OrderCardApp) {}
    fun showMessageOperations(position: Int) {}
    fun myHomeListAdd(position: Int) {}

    fun requestMoneyOperation(item: RmList) {}
    fun justOperation() {}
    fun dialogDismiss() {}
    fun openNotification(item: Notification) {}
    fun openTransferList(item: PopularTransfers) {}
    fun openCreditDetails(item: CreditProduct) {}
    fun openDepositDetails(item: ClientDeposit) {}
    fun openClientCreditPage(item: CreditProduct) {}
    fun updateTemplateList(list: ArrayList<Template>) {}
    fun templateOperation(position: Int, item: Template) {}
    fun selectTemplateType(templateType: String) {}
    fun addTemplateName(toString: String) {}
    fun humoOperationRemove(){}
    fun humoOperationPay(){}
    fun openSearchItem(searchItem: SearchItem) {}
    fun confirmTakeLoan(item: CreditProduct) {}
    fun deviceDelete(){}
    fun deviceState(){}
    fun deviceDeleteAll(){}

}