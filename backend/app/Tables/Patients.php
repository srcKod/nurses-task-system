<?php

namespace App\Tables;

use App\Models\Patient;
use Illuminate\Http\Request;
use ProtoneMedia\Splade\AbstractTable;
use ProtoneMedia\Splade\Facades\Toast;
use ProtoneMedia\Splade\SpladeTable;

class Patients extends AbstractTable
{
    /**
     * Create a new instance.
     *
     * @return void
     */
    public function __construct()
    {
        //
    }

    /**
     * Determine if the user is authorized to perform bulk actions and exports.
     *
     * @return bool
     */
    public function authorize(Request $request)
    {
        return true;
    }

    /**
     * The resource or query builder.
     *
     * @return mixed
     */
    public function for()
    {
        return Patient::query();
    }

    /**
     * Configure the given SpladeTable.
     *
     * @param \ProtoneMedia\Splade\SpladeTable $table
     * @return void
     */
    public function configure(SpladeTable $table)
    {
        $table
            ->withGlobalSearch(columns: ['id','name'])
            ->selectFilter('is_stopped',['1'=>'Stopped','0'=>'Not Stopped'],'Status')
            ->column('id', sortable: true)
            ->column('name',sortable: true, searchable: true)
            ->column('room_photo_path','Room Photo')
            ->column('is_stopped','Status')
            ->column('action')
            ->export()
            ->bulkAction(
                label: 'Delete Selected Patients',
                each: fn (Patient $patient) => $patient->delete(),
                before: fn () => info('Deleteing the selected Patients'),
                after: fn () => Toast::info('Patients Deleted Permanently!'),
                confirm: true
            )
            ->paginate(15);
            // ->searchInput()
            // ->selectFilter()
            // ->withGlobalSearch()

            // ->bulkAction()
            // ->export()
    }
}
