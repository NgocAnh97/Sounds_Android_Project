package com.example.sound_app

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.mtechviral.mplaylib.MusicFinder
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@Suppress("DEPRECATION")
class MusicActivity : AppCompatActivity() {

    var albumArt: ImageView? = null

    var playButton: ImageButton? = null
    var shuffleButton: ImageButton? = null

    var songTitle: TextView? = null
    var songArtist: TextView? = null

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //Yeu cau quyen truy cap Doc tu bo nho ngoai
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        } else {
            createPlayer()
        }
    }

    //Ham tra ve ket qua yeu cau quyen doc tu bo nho
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Neu yeu cau duoc thoa man
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createPlayer()
        } else {
            longToast("Permission not granted. Shut down!")
            finish()
        }
    }

    //Ham tao phan mem phat nhac
    private fun createPlayer() {

        var songsJob = async {
            val songFinder = MusicFinder(contentResolver)
            songFinder.prepare()
            songFinder.allSongs
        }



        launch(kotlinx.coroutines.experimental.android.UI) {
            val songs = songsJob.await()

            val playerUI = object : AnkoComponent<MusicActivity> {
                @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
                override fun createView(ui: AnkoContext<MusicActivity>) = with(ui) {

                    relativeLayout {
                        backgroundResource = R.drawable.purple2_background


                        //Hinh anh album bai hat
                        albumArt = imageView {
                            scaleType = ImageView.ScaleType.FIT_CENTER
                        }.lparams(matchParent, matchParent)


                        verticalLayout {
                            //Layout gom: tieu de, ca sy
                            songTitle = textView {
                                textColor = Color.WHITE
                                typeface = Typeface.DEFAULT_BOLD
                                textSize = 20f
                            }

                            songArtist = textView {
                                textColor = Color.WHITE
                                typeface = Typeface.SERIF
                            }

                            linearLayout {
                                //Layout gom: nut nhan choi/ngung, doi bai hat ngau nhien trong danh sach
                                playButton = imageButton {
                                    backgroundColor = Color.parseColor("#F8B351C9")
                                    imageResource = R.drawable.ic_play_arrow_black
                                    onClick{
                                        playOrPause()
                                    }
                                }.lparams(0, wrapContent, 0.5f){
                                    rightMargin = dip(5)
                                }


                                shuffleButton = imageButton {
                                    backgroundColor = Color.parseColor("#F8B351C9")
                                    imageResource = R.drawable.ic_shuffle_black
                                    onClick {
                                        playRandom()
                                    }
                                }.lparams(0, wrapContent, 0.5f){
                                    leftMargin = dip(5)
                                }
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(5)
                            }

                        }.lparams(matchParent, wrapContent) {
                            topPadding = dip(5)
                            horizontalMargin = dip(5)
//                            alignParentTop()
                        }
                    }
                }

                //Ham cho button Doi bai hat ngau nhien trong danh sach
                fun playRandom() {
                    songs.shuffle()
                    val song = songs[0]
                    mediaPlayer?.reset()
                    mediaPlayer = MediaPlayer.create(ctx, song.uri)
                    mediaPlayer?.setOnCompletionListener {
                        playRandom()
                    }
                    albumArt?.imageURI = song.albumArt
                    songTitle?.text = song.title
                    songArtist?.text = song.artist
                    mediaPlayer?.start()
                    playButton?.imageResource = R.drawable.ic_pause_black
                }

                //Ham cho button phat/ngung bai hat hien tai
                fun playOrPause() {
                    var songPlaying: Boolean? = mediaPlayer?.isPlaying

                    if (songPlaying == true) {
                        mediaPlayer?.pause()
                        playButton?.imageResource = R.drawable.ic_play_arrow_black
                    } else {
                        mediaPlayer?.start()
                        playButton?.imageResource = R.drawable.ic_pause_black
                    }
                }
            }

            //Dat noi dung UI phan mem Music cho Activity
            playerUI.setContentView(this@MusicActivity)
            playerUI.playRandom()
        }
    }

    //Ham thoat app
    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
