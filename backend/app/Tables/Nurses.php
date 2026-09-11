<?php

namespace App\Tables;

use App\Models\Nurse;
use Illuminate\Http\Request;
use ProtoneMedia\Splade\AbstractTable;
use ProtoneMedia\Splade\SpladeTable;

class Nurses extends AbstractTable
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
        return Nurse::query();
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
            ->withGlobalSearch(columns: ['id','name','phone', 'email'])
            ->selectFilter('is_admin',['1'=>'admin','0'=>'user'],'Role')
            ->selectFilter('is_resigned',['1'=>'resigned','0'=>'not resigned'],'Status')
            ->column('id',sortable: true)
            ->column('name',sortable: true, searchable: true)
            ->column('gender')
            ->column('phone', searchable: true)
            ->column('is_resigned','Status')
            ->column('is_admin','Role')
            ->column('email',sortable: true, searchable: true)
            ->column('action')
            ->export()
            ->paginate(15);
            // ->searchInput()
            // ->selectFilter()
            // ->withGlobalSearch()

            // ->bulkAction()
            // ->export()
    }
}
