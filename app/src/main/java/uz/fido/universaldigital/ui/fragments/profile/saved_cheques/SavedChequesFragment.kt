package uz.fido.universaldigital.ui.fragments.profile.saved_cheques

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentSavedChequesBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.utility.fragment.pop
import java.io.File

@AndroidEntryPoint
class SavedChequesFragment : BaseFragment<FragmentSavedChequesBinding, MenuProfileViewModel>(
    FragmentSavedChequesBinding::inflate, MenuProfileViewModel::class.java
), BaseInterface {
    private val adapterGroup by lazy { SavedChequesAdapter(arrayListOf(), requireContext(), this) }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSavedCheques()
        setList(getAppSpecificPdfFiles())
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    override fun openCheque(file: File) {
        if (file.exists()) {
            val context = this // or your activity context
            val uri = FileProvider.getUriForFile(requireContext(), requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                toast(getString(R.string.file_not_exist))
            }
        } else {
            toast(getString(R.string.file_not_exist))
        }
    }

    private fun share(file: File) {
        try {
            // Get URI for the file (use FileProvider if it's in private storage)
            val uri = FileProvider.getUriForFile(requireContext(), requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", file)
            // Create an intent to share the file
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            // Grant temporary permission to read the file
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Start the share intent
            startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun chequeOperations(file: File) {
        val dialog = SavedChequeOperationDialog(file,
            onDeleteClicked = {
                if (deletePdfFile(file)) {
                    toast(getString(R.string.file_deleted_successfully))
                    setList(getAppSpecificPdfFiles())
                } else {
                    toast(getString(R.string.unknown))
                }
            },
            onViewClicked = {
                openCheque(file)
            },
            onShareClicked = {
                share(file)
            })
        dialog.show(childFragmentManager, "")
    }

    private fun deletePdfFile(file: File): Boolean {
        return if (file.exists()) {
            file.delete()
        } else {
            false // File does not exist
        }
    }

    private fun initSavedCheques() {
        binding.savedCheques.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterGroup
        }
    }

    private fun getAppSpecificPdfFiles(): List<File> {
        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val list = downloadsFolder?.listFiles { file -> file.isFile && file.name.endsWith(".pdf") && file.name.startsWith("Universalbank") }?.toList() ?: emptyList()
            return list.ifEmpty { listOf() }
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf()
        }
    }

    private fun setList(list: List<File>) {
        initEmptyView(list)
        adapterGroup.setList(list.asReversed())
    }

    private fun initEmptyView(list: List<File>) {
        binding.emptyView.isVisible = list.isEmpty()
    }

}