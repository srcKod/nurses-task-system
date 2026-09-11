<?php

namespace App\Http\Controllers;

use App\Exports\CaringsExport;
use App\Models\Caring;
use App\Models\Nurse;
use App\Notifications\SendPushNotification;
use App\Tables\Carings;
use Illuminate\Http\Request;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\Notification;
use Illuminate\Support\Facades\Redirect;
use ProtoneMedia\Splade\SpladeTable;
use Spatie\QueryBuilder\AllowedFilter;
use Spatie\QueryBuilder\QueryBuilder;

class DashboardController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('dashboard',[
            'carings' => Carings::class
        ]);
    }

    public function redirectToApiDocs()
    {
        // Use a relative URL so the redirect works on any host (dev or production).
        // Previously this hard-coded http://localhost:8000 which broke on deployed hosts.
        return Redirect::to('/api/documentation');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
              //return response()
    }

//    public function export()
//    {
//        return (new CaringsExport(auth()->user()->id))->download('carings.xlsx');
//    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }
}
