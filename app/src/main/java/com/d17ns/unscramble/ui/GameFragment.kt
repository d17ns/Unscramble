package com.d17ns.unscramble.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.d17ns.unscramble.R
import com.d17ns.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// fragment berisikan logika dari game
class GameFragment : Fragment() {

    // membuat ViewModel saat pertama kali fragment dibuat
    // jika fragment dibuat ulang, maka akan menerima instance GameViewModel yang dibuat fragment pertama
    private val viewModel: GameViewModel by viewModels()

    // bind instance dengan akses ke views pada layout file game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate file layout XML dan mengembalikan binding instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set viewModel untuk data binding
        // mengijinkan layout untuk mengakses seluruh data dari ViewModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // membuat fragment view sebagai lifecycle owner dari binding
        // digunakan agar binding bisa meng-observasi update dari LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // click listener untuk tombol submit dan skip
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    // verifikasi jawaban dan update score jika jawaban benar
    // kemudian menampilkan kata acak berikutnya
    // setelah kata terakhir, akan ditampilkan final score
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    // skip kata tanpa mengubah score
    // kata yang di-skip dianggap nilai 0
    // menampilkan kata acak berikutnya
    // setelah kata terakhir, akan ditampilkan final score
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    // fungsi untuk restart game
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    // fungsi untuk keluar dari game
    private fun exitGame() {
        activity?.finish()
    }

    // fungsi untuk set dan reset error status pada kolom
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    // menmbuat dan menampilkan final score dengan AlertDialog
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }
}