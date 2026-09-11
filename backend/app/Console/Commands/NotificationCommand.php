<?php

namespace App\Console\Commands;

use App\Models\Caring;
use App\Models\Caringtype;
use App\Models\Nurse;
use App\Models\Patient;
use App\Notifications\SendPushNotification;
use Illuminate\Console\Command;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Notification;

class NotificationCommand extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'app:notification-command';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Command description';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $carings = Caring::get();
        foreach($carings as $caring){
            if(Carbon::parse($caring->time,'Asia/Damascus')->diffInMinutes(Carbon::now('Asia/Damascus')) == 0){
                //Send notification
                $nurse = Nurse::where('id', $caring->nurse_id)->whereNotNull('fcm_token')->first();
                $caringtype = Caringtype::where('id', $caring->caringtype_id)->first()->name;
                $patient = Patient::where('id', $caring->patient_id)->first()->name;

                if($nurse != null) {
                    $fcmTokens = $nurse->pluck('fcm_token')->toArray();

                    $title = "Caring Reminder";
                    $body = $caringtype . " " . "for" . " " . $patient;
                    Notification::send($nurse,
                        new SendPushNotification($title, $body, $fcmTokens));
                }
            }
        }
    }
}
