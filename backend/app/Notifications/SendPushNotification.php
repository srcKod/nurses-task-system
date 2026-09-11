<?php

namespace App\Notifications;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Notifications\Messages\MailMessage;
use Illuminate\Notifications\Notification;
use NotificationChannels\Fcm\Exceptions\CouldNotSendNotification;
use NotificationChannels\Fcm\FcmChannel;
use NotificationChannels\Fcm\FcmMessage;
use NotificationChannels\Fcm\Resources\AndroidConfig;
use NotificationChannels\Fcm\Resources\AndroidFcmOptions;
use NotificationChannels\Fcm\Resources\AndroidNotification;
use NotificationChannels\Fcm\Resources\ApnsConfig;
use NotificationChannels\Fcm\Resources\ApnsFcmOptions;

class SendPushNotification extends Notification
{
    use Queueable;

    protected $title;
    protected $body;
    protected $fcmTokens;
    /**
     * Create a new notification instance.
     */
    public function __construct($title, $body, $fcmTokens)
    {
        $this->title = $title;
        $this->body = $body;
        $this->fcmTokens = $fcmTokens;
    }

    /**
     * Get the notification's delivery channels.
     *
     * @return array<int, string>
     */
    public function via($notifiable)
    {
        return [
            FcmChannel::class
        ];
    }

    /**
     * Get the array representation of the notification.
     *
     * @param  mixed  $notifiable
     * @return array
     */
//    public function toDatabase($notifiable)
//    {
//        return [
//            'title' => $this->title,
//            'body' => $this->body,
//        ];
//    }

    /**
     * @throws CouldNotSendNotification
     */
    public function toFcm($notifiable): FcmMessage
    {
        $androidConfig = AndroidConfig::create()
            ->setFcmOptions(AndroidFcmOptions::create()->setAnalyticsLabel('analytics'));

        $iosConfig = ApnsConfig::create()
            ->setFcmOptions(ApnsFcmOptions::create()->setAnalyticsLabel('analytics_ios'));

        return FcmMessage::create()
                ->setNotification(\NotificationChannels\Fcm\Resources\Notification::create()
                    ->setTitle($this->title)
                    ->setBody($this->body)
                    ->setImage('https://example.com/public/files/logo.png'))
                ->setAndroid($androidConfig)
                ->setApns($iosConfig)
                ->setData([
                    'category' => 'Carings',
                    'title' => $this->title,
                    'body' => $this->body,
                ]);
    }

    /**
     * Get the array representation of the notification.
     *
     * @param  mixed  $notifiable
     * @return array
     */
    public function toArray($notifiable)
    {
        return [
            //
        ];
    }
}
