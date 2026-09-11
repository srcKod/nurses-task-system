<?php

namespace App\Jobs;

use App\Notifications\SendPushNotification;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldBeUnique;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\Notification;

class NotificationJob implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    protected $nurse;
    protected $fcmTokens;
    /**
     * Create a new job instance.
     */
    public function __construct($nurse, $fcmTokens)
    {
        $this->nurse=$nurse;
        $this->fcmTokens=$fcmTokens;
    }

    /**
     * Execute the job.
     */
    public function handle(): void
    {
        $name = explode(" ", $this->nurse->name)[0];
        $title = "Caring Update";
        $body = "Dear". " " . $name . " " . "a new caring was added please check!";
        Notification::send($this->nurse,
            new SendPushNotification($title, $body, $this->fcmTokens));
    }
}
